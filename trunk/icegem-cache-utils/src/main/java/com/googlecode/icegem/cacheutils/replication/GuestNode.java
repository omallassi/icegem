package com.googlecode.icegem.cacheutils.replication;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;
import com.googlecode.icegem.cacheutils.monitor.utils.Utils;

/**
 * The cache server which is used for replication test. Connects to the cluster
 * represented by a locator, puts its locator as a key and current time as a
 * value to the technical region and waits events from other GuestNodes. After
 * that calculates time of replication ant prints it to system output.
 * 
 * Returns 0 as exit code in case of all the expected events received, 1
 * otherwise.
 * 
 */
public class GuestNode {
	private static final int CHECK_PERIOD = 50;

	private static final String KEY_PREFIX = "check-replication-";

	private static final String KEY_POSTFIX_STARTED_AT = "-startedAt";
	private static final String KEY_POSTFIX_SENT_AT = "-sentAt";
	private static final String KEY_POSTFIX_DURATION = "-receivedAt";

	/* Local cluster */
	private String localClusterName;

	/* Cache instance */
	private ClientCache clientCache;

	/* Technical region instance */
	private Region<String, Long> region;

	/* Path to the license file */
	private String licenseFile;

	private String licenseType;

	/* The name of technical region */
	private String regionName;

	private Properties clustersProperties;

	private boolean debugEnabled;

	private long processingStartedAt;

	private boolean quiet;

	/**
	 * Creates the instance of guest node
	 * 
	 * @param clustersProperties
	 *            - the list of locators of remote clusters
	 * @param licenseFile
	 *            - the path to license file
	 * @param regionName
	 *            - the name of the technical region
	 * @param processingStartedAt
	 */
	private GuestNode(String cluster, Properties clustersProperties,
		String licenseFile, String licenseType, String regionName,
		boolean debugEnabled, boolean quiet, long processingStartedAt) {

		debug("GuestNode#GuestNode(String, Properties, String, String, String): Creating instance with parameters: cluster = "
			+ cluster
			+ ", clustersProperties = "
			+ clustersProperties
			+ ", licenseFile = "
			+ licenseFile
			+ ", licenseType = "
			+ licenseType + ", regionName = " + regionName);

		this.localClusterName = cluster;
		this.clustersProperties = clustersProperties;
		this.licenseFile = licenseFile;
		this.licenseType = licenseType;
		this.regionName = regionName;
		this.debugEnabled = debugEnabled;
		this.quiet = quiet;
		this.processingStartedAt = processingStartedAt;

		debug("GuestNode#GuestNode(String, Properties, String, String, String): Creating RelationsController");

		init();
	}

	private String createStartedAtKey(String clusterName) {
		return KEY_PREFIX + clusterName + KEY_POSTFIX_STARTED_AT;
	}

	private String createSentAtKey(String clusterName) {
		return KEY_PREFIX + clusterName + KEY_POSTFIX_SENT_AT;
	}

	private String createReceivedAtKey(String fromClusterName,
		String toClusterName) {

		return KEY_PREFIX + fromClusterName + "-" + toClusterName
			+ KEY_POSTFIX_DURATION;
	}

	/**
	 * Initializes the technical region and puts the local entry with its
	 * locator as a key and current time as a value
	 */
	private void init() {
		try {
			debug("GuestNode#init(): Creating Cache");

			ClientCacheFactory clientCacheFactory = new ClientCacheFactory();

			if ((licenseFile != null) && (licenseType != null)) {
				clientCacheFactory.set("license-file", licenseFile).set(
					"license-type", licenseType);
			}

			clientCacheFactory.set("log-level", "none")
				.setPoolSubscriptionEnabled(true);

			String locators = clustersProperties.getProperty(localClusterName);
			String[] locatorsArray = locators.split(",");
			for (String locator : locatorsArray) {
				String locatorHost = locator.substring(0, locator.indexOf("["));

				String locatorPortString = locator.substring(
					locator.indexOf("[") + 1, locator.indexOf("]"));
				int locatorPort = Integer.parseInt(locatorPortString);

				debug("GuestNode#init(): Adding locator to pool: locatorHost = "
					+ locatorHost + ", locatorPort = " + locatorPort);

				clientCacheFactory.addPoolLocator(locatorHost, locatorPort);
			}

			clientCache = clientCacheFactory.create();

			ClientRegionFactory<String, Long> clientRegionFactory = clientCache
				.createClientRegionFactory(ClientRegionShortcut.PROXY);

			region = clientCache.getRegion(regionName);

			debug("GuestNode#init(): Get region with name = " + regionName
				+ ": region = " + region);

			if (region == null) {
				region = clientRegionFactory.create(regionName);
			}
			debug("GuestNode#init(): Create region with name = " + regionName
				+ ": region = " + region);

		} catch (Throwable t) {
			debug(
				"GuestNode#init(): Throwable caught with message = "
					+ t.getMessage(), t);

		}
	}

	private void waitForStarted() {
		debug("GuestNode#waitForStarted(): Waiting for other clusters started");

		while (true) {
			boolean othersStarted = true;

			for (Object key : clustersProperties.keySet()) {
				String clusterName = (String) key;

				Long startedAt = region.get(createStartedAtKey(clusterName));

				debug("GuestNode#waitForStarted(): Checking startedAt: startedAt = "
					+ startedAt
					+ ", processingStartedAt = "
					+ processingStartedAt);

				if ((startedAt == null)
					|| (startedAt.longValue() < processingStartedAt)) {

					othersStarted = false;
					break;

				}
			}

			if (othersStarted) {
				break;
			}

			try {
				TimeUnit.MILLISECONDS.sleep(CHECK_PERIOD);
			} catch (InterruptedException e) {
			}
		}

		debug("GuestNode#waitForStarted(): Other clusters started");
	}

	private void waitForSent() {
		debug("GuestNode#waitForSent(): Waiting for other clusters sent");

		while (true) {
			boolean othersSent = true;

			Map<String, Long> clusterNameToReceivedAtMap = new HashMap<String, Long>();
			for (Object key : clustersProperties.keySet()) {
				String clusterName = (String) key;

				if (localClusterName.equals(clusterName)) {
					continue;
				}

				Long sentAt = region.get(createSentAtKey(clusterName));
				long receivedAt = System.currentTimeMillis();

				if ((sentAt != null)
					&& (sentAt.longValue() > processingStartedAt)) {

					clusterNameToReceivedAtMap.put(clusterName, receivedAt);
				}
			}

			for (Object key : clustersProperties.keySet()) {
				String clusterName = (String) key;

				if (localClusterName.equals(clusterName)) {
					continue;
				}

				Long receivedAt = clusterNameToReceivedAtMap.get(clusterName);

				if (receivedAt == null) {

					if (othersSent) {
						othersSent = false;
					}

				} else {
					region.put(
						createReceivedAtKey(clusterName, localClusterName),
						receivedAt);
				}
			}

			if (othersSent) {
				break;
			}

			try {
				TimeUnit.MILLISECONDS.sleep(CHECK_PERIOD);
			} catch (InterruptedException e) {
			}
		}

		debug("GuestNode#waitForSent(): Other clusters sent");
	}

	private void waitForConnected() {
		debug("GuestNode#waitForConnected(): Waiting for all the clusters connected");

		while (true) {
			boolean connected = true;

			for (Object fromKey : clustersProperties.keySet()) {
				String fromClusterName = (String) fromKey;

				for (Object toKey : clustersProperties.keySet()) {
					String toClusterName = (String) toKey;

					if (fromClusterName.equals(toClusterName)) {
						continue;
					}

					Long receivedAt = region.get(createReceivedAtKey(
						fromClusterName, toClusterName));

					if (receivedAt == null) {
						connected = false;
						break;
					}
				}
			}

			if (connected) {
				break;
			}

			try {
				TimeUnit.MILLISECONDS.sleep(CHECK_PERIOD);
			} catch (InterruptedException e) {
			}
		}

		debug("GuestNode#waitForConnected(): All the clusters connected");
	}

	/**
	 * Periodically checks the status of connections to the remote clusters
	 */
	private class ConnectionCheckTask implements Runnable {
		private boolean connected = false;

		public void run() {
			try {
				region.put(createStartedAtKey(localClusterName),
					System.currentTimeMillis());

				waitForStarted();

				region.put(createSentAtKey(localClusterName),
					System.currentTimeMillis());

				waitForSent();

				waitForConnected();

				connected = true;
			} catch (Throwable t) {
				connected = false;
			}
		}

		public boolean isConnected() {
			return connected;
		}
	}

	/**
	 * Waits for responses from all the remote clusters. If they don't respond
	 * in timeout milliseconds, interrupts the process.
	 * 
	 * @param timeout
	 *            - the timeout in milliseconds
	 * @return - true if the connection process finished successfully, false
	 *         otherwise
	 */
	public boolean waitFor(long timeout) {
		debug("GuestNode#waitFor(long): Waiting for task finish with timeout = "
			+ timeout);

		ConnectionCheckTask connectionCheckTask = new ConnectionCheckTask();

		Utils.execute(connectionCheckTask, timeout);

		boolean connected = connectionCheckTask.isConnected();

		debug("GuestNode#waitFor(long): Task finished connected = " + connected);

		return connected;
	}

	/**
	 * Finalizes work with the guest node
	 */
	public void close() {
		try {
			debug("GuestNode#close(): Closing the cache");

			clientCache.close();

			debug("GuestNode#close(): Cache closed = " + clientCache.isClosed());
		} catch (Throwable t) {
			debug(
				"GuestNode#close(): Throwable caught with message = "
					+ t.getMessage(), t);
		}
	}

	/**
	 * Prints the current state of connections
	 * 
	 * @param connected
	 */
	public void printState(boolean connected) {
		if (!quiet) {

			StringBuilder sb = new StringBuilder();

			if (connected) {

				sb.append(localClusterName).append(" <= ");

				Iterator<Object> it = clustersProperties.keySet().iterator();
				while (it.hasNext()) {
					String clusterName = (String) it.next();

					if (localClusterName.equals(clusterName)) {
						continue;
					}

					Long sentAt = region.get(createSentAtKey(clusterName));
					Long receivedAt = region.get(createReceivedAtKey(
						clusterName, localClusterName));

					long duration = receivedAt - sentAt;

					sb.append("[").append(clusterName).append(", ")
						.append(duration).append("ms]");
				}

			} else {

				sb.append("Connection process is not finished for ").append(
					localClusterName);

			}

			System.out.println(sb.toString());

		}
	}

	/**
	 * Configures and starts the guest node
	 * 
	 * @param args
	 *            - the configuration arguments
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 9) {
				System.exit(1);
			}

			String cluster = args[0];
			Properties clustersProperties = PropertiesHelper
				.stringToProperties(args[1]);
			long timeout = Long.parseLong(args[2]);
			String licenseFile = (args[3] == "null" ? null : args[3]);
			String licenseType = args[4];
			String regionName = args[5];
			boolean debugEnabled = ("true".equals(args[6]) ? true : false);
			boolean quiet = ("true".equals(args[7]) ? true : false);
			long processingStartedAt = Long.parseLong(args[8]);

			GuestNode guestNode = new GuestNode(cluster, clustersProperties,
				licenseFile, licenseType, regionName, debugEnabled, quiet,
				processingStartedAt);

			boolean connected = guestNode.waitFor(timeout);

			guestNode.printState(connected);

			guestNode.close();

			int exitCode = connected ? 0 : 1;

			System.exit(exitCode);
		} catch (Throwable t) {
			System.exit(1);
		}
	}

	private void debug(String message) {
		debug(message, null);
	}

	private void debug(String message, Throwable t) {
		if (debugEnabled) {
			long currentTime = System.currentTimeMillis();
			long timeSinceProcessingStart = currentTime - processingStartedAt;
			System.err.println(timeSinceProcessingStart + " ["
				+ localClusterName + "] " + message);

			if (t != null) {
				t.printStackTrace(System.err);
			}
		}
	}
}
