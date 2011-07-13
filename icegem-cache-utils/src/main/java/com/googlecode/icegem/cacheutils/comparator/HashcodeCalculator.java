package com.googlecode.icegem.cacheutils.comparator;

import java.util.List;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolFactory;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.googlecode.icegem.cacheutils.common.Utils;

public class HashcodeCalculator {

	private Pool createLocatorsPool(String locators) {
		PoolFactory poolFactory = PoolManager.createFactory();

		for (String locator : locators.split(",")) {
			String host = locator.substring(0, locator.indexOf("["));
			int port = Integer.parseInt(locator.substring(
				locator.indexOf("[") + 1, locator.indexOf("]")));

			poolFactory.addLocator(host, port);
		}

		return poolFactory.create("pool-" + locators);
	}

	private Pool createServerPool(String server) {
		String host = server.substring(0, server.indexOf("["));
		int port = Integer.parseInt(server.substring(server.indexOf("[") + 1,
			server.indexOf("]")));

		return PoolManager.createFactory().addServer(host, port)
			.create("pool-" + host + "-" + port);
	}

	private long calculatePartitionHashcode(String locators, String regionName,
		int loadFactor, List<String> packages) {
		ClientCache cache = new ClientCacheFactory().set("log-level", "none")
			.create();

		ClientRegionFactory<?, ?> clientRegionFactory = cache
			.createClientRegionFactory(ClientRegionShortcut.PROXY);

		Pool pool = createLocatorsPool(locators);

		clientRegionFactory.setPoolName(pool.getName());

		Region<?, ?> region = clientRegionFactory.create(regionName);

		HashcodeResultCollector collector = (HashcodeResultCollector) FunctionService
			.onRegion(region).withCollector(new HashcodeResultCollector())
			.withArgs(new HashcodeFunctionArguments(loadFactor, packages))
			.execute(new HashcodeFunction());

		long hashcode = (Long) collector.getResult();

		cache.close();

		return hashcode;
	}

	private long calculateReplicateHashcode(String locators, String regionName,
		int loadFactor, List<String> packages) {
		ClientCache cache = new ClientCacheFactory()
			.set("log-level", "warning").create();

		ClientRegionFactory<?, ?> clientRegionFactory = cache
			.createClientRegionFactory(ClientRegionShortcut.PROXY);

		Pool pool = createServerPool(locators);

		clientRegionFactory.setPoolName(pool.getName());

		Region<?, ?> region = clientRegionFactory.create(regionName);

		HashcodeResultCollector collector = (HashcodeResultCollector) FunctionService
			.onRegion(region).withCollector(new HashcodeResultCollector())
			.withArgs(new HashcodeFunctionArguments(loadFactor, packages))
			.execute(new HashcodeFunction());

		long hashcode = (Long) collector.getResult();

		cache.close();

		return hashcode;
	}

	public static void main(String[] args) throws Throwable {
		try {
			String mode = args[0];
			String address = args[1];
			String regionName = args[2];
			String filename = args[3];
			int loadFactor = Integer.parseInt(args[4]);
			List<String> packages = Utils.csvToStringList(args[5]);

			HashcodeCalculator calculator = new HashcodeCalculator();

			long hashcode;

			if (CompareTool.PARTITION.equals(mode)) {
				hashcode = calculator.calculatePartitionHashcode(address,
					regionName, loadFactor, packages);
			} else {
				hashcode = calculator.calculateReplicateHashcode(address,
					regionName, loadFactor, packages);
			}

			FileService.write(filename, String.valueOf(hashcode));
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

}
