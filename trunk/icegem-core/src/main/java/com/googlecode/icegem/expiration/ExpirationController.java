package com.googlecode.icegem.expiration;

import java.util.List;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;

public class ExpirationController {

	private ClientCache cache;
	private ClientRegionFactory<Object, Object> clientRegionFactory;

	public ExpirationController(String locatorHost, int locatorPort) {
		cache = new ClientCacheFactory().set("log-level", "warning").addPoolLocator(locatorHost, locatorPort).create();

		clientRegionFactory = cache
			.createClientRegionFactory(ClientRegionShortcut.PROXY);
	}

	public long process(String regionName, ExpirationPolicy policy) {
		Region<Object, Object> region = cache.getRegion(regionName);

		if (region == null) {
			region = clientRegionFactory.create(regionName);
		}

		long destroyedEntriesNumber = 0;
		ResultCollector<?, ?> collector = FunctionService.onRegion(region).execute(new ExpirationFunction(policy));
		Object result = collector.getResult();
		if (result instanceof List<?>) {
			List<?> resultList = (List<?>) result;
			Object resultObject = resultList.get(0);
			if (resultObject instanceof Long) {
				 destroyedEntriesNumber = (Long) resultObject;
			}
		}
		
		return destroyedEntriesNumber;
	}
	
	public void close() {
		cache.close();
	}
}
