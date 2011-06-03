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

public class GuestNode {

	private String locators;
	private Cache cache;
	private Region<String, Long> region;
	private RelationsController relationsController;
	private String licenseFile;
	private String regionName;

	private GuestNode(String localLocators, String remoteLocators, String licenseFile, String regionName) {
		this.locators = localLocators;
		this.licenseFile = licenseFile;
		this.regionName = regionName;

		this.relationsController = new RelationsController(localLocators,
			new HashSet<String>(Arrays.asList(remoteLocators.split(","))));

		init();
	}

	private void init() {
		cache = new CacheFactory()
			.set("license-file", licenseFile)
			.set("mcast-port", "0").set("log-level", "none")
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

					if (!locators.equals(fromLocators)) {
						long duration = receivedAt - sentAt;
						relationsController.get(fromLocators).setDuration(
							duration);
					}
				}
			});

		region.put(locators, System.currentTimeMillis());
	}

	private class ConnectionCheckTask implements Runnable {
		private boolean connected = false;

		public void run() {
			while (!relationsController.isConnected()) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			connected = true;
		}

		public boolean isConnected() {
			return connected;
		}
	}

	public boolean waitFor(long timeout) {

		ConnectionCheckTask connectionCheckTask = new ConnectionCheckTask();

		Utils.execute(connectionCheckTask, timeout);

		boolean connected = connectionCheckTask.isConnected();

		return connected;
	}

	public void close() {
		cache.close();
	}

	public void printState() {
		System.out.println(relationsController);
	}

	public static void main(String[] args) {
		if (args.length != 5) {
			System.exit(1);
		}

		String localLocators = args[0];
		String remoteLocators = args[1];
		long timeout = Long.parseLong(args[2]);
		String licenseFile = args[3];
		String regionName = args[4];

		GuestNode guestNode = new GuestNode(localLocators, remoteLocators, licenseFile, regionName);

		boolean connected = guestNode.waitFor(timeout);

		guestNode.printState();

		guestNode.close();

		System.exit(connected ? 0 : 1);
	}
}
