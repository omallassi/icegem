package com.griddynamics.gemfire.cacheutils.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.gemstone.gemfire.admin.AdminDistributedSystem;
import com.gemstone.gemfire.admin.AdminDistributedSystemFactory;
import com.gemstone.gemfire.admin.AdminException;
import com.gemstone.gemfire.admin.DistributedSystemConfig;
import com.gemstone.gemfire.admin.Statistic;
import com.gemstone.gemfire.admin.SystemMember;
import com.gemstone.gemfire.admin.SystemMemberCache;
import com.gemstone.gemfire.admin.SystemMemberRegion;
import com.gemstone.gemfire.distributed.DistributedSystem;

/**
 * Connects to the distributed system and explore its' structure.
 */
public class AdminService {
	private AdminDistributedSystem admin;
	private Set<String> regionNames = new HashSet<String>();
	private Set<SystemMember> systemMembers = new HashSet<SystemMember>();

	public AdminService(String locator) throws Exception {
		this.admin = adminCreateAndConnect(locator);
		systemMembers.addAll(new HashSet<SystemMember>(Arrays.asList(admin.getCacheVms())));
		systemMembers.addAll(new HashSet<SystemMember>(Arrays.asList(admin.getSystemMemberApplications())));
	}

	public Set<String> getRegionNames(String regionNamesOption,	boolean withSubRegionsOption) throws AdminException {
		if (regionNamesOption.equals("all"))
			return getSystemRegionNames(null);
		String[] regionNamesArray = regionNamesOption.split(",");
		if (!withSubRegionsOption)
			return new HashSet<String>(Arrays.asList(regionNamesArray));
		return getSystemRegionNames(regionNamesArray);
	}

	public Map<String, SystemMemberRegion> getMemberRegionMap(String name)
			throws AdminException {
		Map<String, SystemMemberRegion> result = new HashMap<String, SystemMemberRegion>();
		for (SystemMember member : systemMembers) {
			if (!member.hasCache())
				continue;
			SystemMemberCache cache = member.getCache();
			Statistic[] st = cache.getStatistics();
			SystemMemberRegion region = cache.getRegion(name);
			if (region != null) {
				result.put(member.getId(), region);
			}
		}
		return result;
	}

    public void close() {
		admin.disconnect();
	}

	private Set<String> getSystemRegionNames(Object[] regionNamesArray)
			throws AdminException {
		for (SystemMember member : systemMembers) {
			if (!member.hasCache())
				continue;
			SystemMemberCache cache = member.getCache();
			if (regionNamesArray == null)
				regionNamesArray = cache.getRootRegionNames().toArray();
			for (Object name : regionNamesArray) {
				SystemMemberRegion region = cache.getRegion(name.toString());
				if (region != null) {
					regionNames.add(name.toString());
					regionNames.addAll(region.getSubregionNames());
				}
			}
		}
		return regionNames;
	}

	private AdminDistributedSystem adminCreateAndConnect(String locator)
			throws Exception {
		Properties props = new Properties();
		props.setProperty("mcast-port", "0");
		props.setProperty("locators", locator);
		AdminDistributedSystemFactory.setEnableAdministrationOnly(true);
		DistributedSystem connection = DistributedSystem.connect(props);
		DistributedSystemConfig config = AdminDistributedSystemFactory
				.defineDistributedSystem(connection, null);
		AdminDistributedSystem admin = AdminDistributedSystemFactory
				.getDistributedSystem(config);
		admin.connect();
		long timeout = 30 * 1000;
		try {
			if (!admin.waitToBeConnected(timeout)) {
				String s = "Could not connect after " + timeout + "ms";
				throw new Exception(s);
			}
		} catch (InterruptedException ex) {
			String s = "Interrupted while waiting to be connected";
			throw new Exception(s, ex);
		}
		return admin;
	}

}
