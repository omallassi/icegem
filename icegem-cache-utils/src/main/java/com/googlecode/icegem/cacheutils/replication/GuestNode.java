package com.googlecode.icegem.cacheutils.replication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.RegionShortcut;
import com.gemstone.gemfire.cache.Scope;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
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
	// private static final Logger log = Logger.getLogger(GuestNode.class);

	/* Local locators */
	private String locators;

	/* Cache instance */
	private Cache cache;

	/* Technical region instance */
	private Region<String, Long> region;

	/* Relations controller */
	private RelationsController relationsController;

	/* Path to the license file */
	private String licenseFile;

	/* The name of technical region */
	private String regionName;

	private final String licenseType;

	/**
	 * Creates the instance of guest node
	 * 
	 * @param localLocators
	 *            - the list of locators to which this guest node will connect
	 * @param remoteLocators
	 *            - the list of locators of remote clusters
	 * @param licenseFile
	 *            - the path to license file
	 * @param regionName
	 *            - the name of the technical region
	 * @param regionName2
	 */
	private GuestNode(String localLocators, String remoteLocators,
		String licenseFile, String licenseType, String regionName) {
		this.locators = localLocators;
		this.licenseFile = licenseFile;
		this.licenseType = licenseType;
		this.regionName = regionName;

		this.relationsController = new RelationsController(localLocators,
			new HashSet<String>(Arrays.asList(remoteLocators.split(","))));

		init();
	}

	/**
	 * Initializes the technical region and puts the local entry with its
	 * locator as a key and current time as a value
	 */
	private void init() {
		CacheFactory cacheFactory = new CacheFactory();

		if ((licenseFile != null) && (licenseType != null)) {
			cacheFactory.set("license-file", licenseFile).set("license-type",
				licenseType);
		}

		cache = cacheFactory.set("mcast-port", "0").set("log-level", "none")
			.set("locators", locators).create();

		region = cache.getRegion(regionName);

		if (region == null) {
			RegionFactory<String, Long> regionFactory = cache
				.createRegionFactory(RegionShortcut.REPLICATE);
			regionFactory.setEnableGateway(true)
				.setScope(Scope.DISTRIBUTED_ACK);

			region = regionFactory.create(regionName);
		}

		region.getAttributesMutator().addCacheListener(
			new CacheListenerAdapter<String, Long>() {
				@Override
				public void afterCreate(EntryEvent<String, Long> event) {
					long receivedAt = System.currentTimeMillis();
					long sentAt = event.getNewValue();
					String fromLocators = event.getKey();

					/*
					 * log.info("afterCreate event: fromLocators = " +
					 * fromLocators + ", localLocators = " + locators);
					 */

					if (!locators.equals(fromLocators)) {
						long duration = receivedAt - sentAt;
						relationsController.get(fromLocators).setDuration(
							duration);
					}
				}
			});

		region.put(locators, System.currentTimeMillis());
	}

	/**
	 * Periodically checks the status of connections to the remote clusters
	 */
	private class ConnectionCheckTask implements Runnable {
		private boolean connected = false;

		public void run() {
			while (!relationsController.isConnected()) {
				// log.info(relationsController);
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
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

		ConnectionCheckTask connectionCheckTask = new ConnectionCheckTask();

		Utils.execute(connectionCheckTask, timeout);

		boolean connected = connectionCheckTask.isConnected();

		return connected;
	}

	/**
	 * Finalizes work with the guest node
	 */
	public void close() {
		cache.close();
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
			// System.out.println("Start");
			if (args.length != 6) {
				/*
				 * System.err
				 * .println("GuestNode#main: misconfiguration, specified " +
				 * args.length + " parameters: " + Arrays.asList(args));
				 * log.warn("GuestNode#main: misconfiguration, specified " +
				 * args.length + " parameters: " + Arrays.asList(args));
				 */
				System.out
					.println("GuestNode#main: misconfiguration, specified "
						+ args.length + " parameters: " + Arrays.asList(args));
				System.exit(1);
			}

			String localLocators = args[0];
			String remoteLocators = args[1];
			long timeout = Long.parseLong(args[2]);
			String licenseFile = (args[3] == "null" ? null : args[3]);
			String licenseType = args[4];
			String regionName = args[5];

			/*
			 * System.err.println("Starting the guest node");
			 * log.info("Starting the guest node");
			 */
			GuestNode guestNode = new GuestNode(localLocators, remoteLocators,
				licenseFile, licenseType, regionName);

			/*
			 * System.err.println("Waiting for the finish");
			 * log.info("Waiting for the finish");
			 */
			boolean connected = guestNode.waitFor(timeout);

			/*
			 * System.err.println("Printing state"); log.info("Printing state");
			 */
			guestNode.printState();

			/*
			 * System.err.println("Closing"); log.info("Closing");
			 */
			guestNode.close();

			int exitCode = connected ? 0 : 1;

			/*
			 * System.err.println("GuestNode#main: exiting with exitCode = " +
			 * exitCode);
			 * 
			 * log.warn("GuestNode#main: exiting with exitCode = " + exitCode);
			 */

			System.out.println("GuestNode#main: exiting with exitCode = "
				+ exitCode);
			System.exit(exitCode);
		} catch (Throwable t) {
			/*
			 * log.warn("GuestNode#main: Throwable catched with message = " +
			 * t.getMessage()); System.err
			 * .println("GuestNode#main: Throwable catched with message = " +
			 * t.getMessage());
			 */
			// t.printStackTrace(System.err);
			System.out
				.println("GuestNode#main: Throwable catched with message = "
					+ t.getMessage());

			System.exit(1);
		}
	}
}
