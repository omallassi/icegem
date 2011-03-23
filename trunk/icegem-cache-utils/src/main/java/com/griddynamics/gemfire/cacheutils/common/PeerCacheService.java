package com.griddynamics.gemfire.cacheutils.common;

import java.util.HashSet;
import java.util.Set;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;

/**
 * Connects to distributes system as client in order to perform some tasks on a certain regions
 */
public class PeerCacheService {
	private ClientCache cache;
	
	public PeerCacheService(String locator) {
		String locatorHost = locator.substring(0, locator.indexOf("["));
		String locatorPort = locator.substring(locator.indexOf("[") + 1, locator.indexOf("]"));
		this.cache = new ClientCacheFactory().addPoolLocator(locatorHost, Integer.parseInt(locatorPort)).create();
	}

	public Set<Region<?, ?>> createRegions(Set<String> regionNames) {
		Set<Region<?, ?>> regions = new HashSet<Region<?, ?>>();   
		ClientRegionFactory proxyRegionFactory = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		for (String regionName : regionNames) {
			Region region = proxyRegionFactory.create(regionName);
			regions.add(region);
		}
		return regions;
	}
	
	public Region<?, ?> createRegion(String regionName) {
		ClientRegionFactory proxyRegionFactory = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		Region region = proxyRegionFactory.create(regionName);
		return region;
	}

	public void close() {
		cache.close();
	}
}
