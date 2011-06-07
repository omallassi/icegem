package com.googlecode.icegem.cacheutils.replication;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.RegionShortcut;
import com.gemstone.gemfire.cache.Scope;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;
import com.googlecode.icegem.cacheutils.monitor.utils.Utils;
import com.googlecode.icegem.cacheutils.replication.relations.RelationsController;

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
	private static final int CONNECTION_CHECK_PERIOD = 50;

	/* Local cluster */
	private String cluster;

	/* Cache instance */
	private Cache cache;

	/* Technical region instance */
	private Region<String, Long> region;

	/* Relations controller */
	private RelationsController relationsController;

	/* Path to the license file */
	private String licenseFile;

	private String licenseType;

	/* The name of technical region */
	private String regionName;

	private final Properties clustersProperties;

	private static boolean debugEnabled;

	/**
	 * Creates the instance of guest node
	 * 
	 * @param localLocators
	 *            - the list of locators to which this guest node will connect
	 * @param clustersProperties
	 *            - the list of locators of remote clusters
	 * @param licenseFile
	 *            - the path to license file
	 * @param regionName
	 *            - the name of the technical region
	 */
	private GuestNode(String cluster, Properties clustersProperties,
		String licenseFile, String licenseType, String regionName) {

		debug("GuestNode#GuestNode(String, Properties, String, String, String): Creating instance with parameters: cluster = "
			+ cluster
			+ ", clustersProperties = "
			+ clustersProperties
			+ ", licenseFile = "
			+ licenseFile
			+ ", licenseType = "
			+ licenseType + ", regionName = " + regionName);

		this.cluster = cluster;
		this.clustersProperties = clustersProperties;
		this.licenseFile = licenseFile;
		this.licenseType = licenseType;
		this.regionName = regionName;

		debug("GuestNode#GuestNode(String, Properties, String, String, String): Creating RelationsController");

		this.relationsController = new RelationsController(cluster,
			clustersProperties);

		init();
	}

	/**
	 * Initializes the technical region and puts the local entry with its
	 * locator as a key and current time as a value
	 */
	private void init() {
		debug("GuestNode#init(): Creating Cache");

		CacheFactory cacheFactory = new CacheFactory();

		if ((licenseFile != null) && (licenseType != null)) {
			cacheFactory.set("license-file", licenseFile).set("license-type",
				licenseType);
		}

		cache = cacheFactory.set("mcast-port", "0").set("log-level", "none")
			.set("locators", clustersProperties.getProperty(cluster)).create();

		region = cache.getRegion(regionName);

		debug("GuestNode#init(): Get region with name = " + regionName
			+ ": region = " + region);

		if (region == null) {
			RegionFactory<String, Long> regionFactory = cache
				.createRegionFactory(RegionShortcut.REPLICATE);
			regionFactory.setEnableGateway(true)
				.setScope(Scope.DISTRIBUTED_ACK);

			region = regionFactory.create(regionName);

			debug("GuestNode#init(): Create region with name = " + regionName
				+ ": region = " + region);
		}

		debug("GuestNode#init(): Add CacheListener to region with name = "
			+ regionName);

		region.getAttributesMutator().addCacheListener(
			new CacheListenerAdapter<String, Long>() {
				@Override
				public void afterCreate(EntryEvent<String, Long> event) {
					long receivedAt = System.currentTimeMillis();
					long sentAt = event.getNewValue();
					String fromCluster = event.getKey();

					debug("GuestNode afterCreate event: fromCluster = "
						+ fromCluster + ", toCluster = " + cluster);

					if (!cluster.equals(fromCluster)) {
						long duration = receivedAt - sentAt;
						relationsController.get(fromCluster).setDuration(
							duration);

						debug("GuestNode afterCreate event: New duration added: "
							+ relationsController.get(fromCluster)
								.getDuration());
					}
				}
			});

		region.put(cluster, System.currentTimeMillis());
	}

	/**
	 * Periodically checks the status of connections to the remote clusters
	 */
	private class ConnectionCheckTask implements Runnable {
		private boolean connected = false;

		public void run() {
			while (!relationsController.isConnected()) {
				try {
					TimeUnit.MILLISECONDS.sleep(CONNECTION_CHECK_PERIOD);
				} catch (InterruptedException e) {
				}
			}
			connected = true;
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
		debug("GuestNode#close(): Closing the cache");

		cache.close();

		debug("GuestNode#close(): Cache closed = " + cache.isClosed());
	}

	/**
	 * Prints the current state of connections
	 */
	public void printState() {
		System.out.println(relationsController);
	}

	/**
	 * Configures and starts the guest node
	 * 
	 * @param args
	 *            - the configuration arguments
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 7) {
				System.exit(1);
			}

			String cluster = args[0];
			Properties clustersProperties = PropertiesHelper
				.stringToProperties(args[1]);
			long timeout = Long.parseLong(args[2]);
			String licenseFile = (args[3] == "null" ? null : args[3]);
			String licenseType = args[4];
			String regionName = args[5];
			debugEnabled = ("true".equals(args[6]) ? true : false);

			GuestNode guestNode = new GuestNode(cluster, clustersProperties,
				licenseFile, licenseType, regionName);

			boolean connected = guestNode.waitFor(timeout);

			guestNode.printState();

			guestNode.close();

			int exitCode = connected ? 0 : 1;

			System.exit(exitCode);
		} catch (Throwable t) {
			System.exit(1);
		}
	}

	private void debug(String message) {
		if (debugEnabled) {
			System.err.println(message);
		}
	}
}
