package com.googlecode.icegem.expiration;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.execute.FunctionService;

/**
 * <p>
 * The class which can expire region entries using custom policy (strategy).
 * </p>
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * 
 * ExpirationController expirationController = new ExpirationController(
 * 	&quot;127.0.0.1&quot;, 10355);
 * 
 * long destroyedEntriesNumberForData = expirationController.process(&quot;data&quot;,
 * 	new ExpirationPolicy() {
 * 
 * 		public boolean isExpired(Entry&lt;Object, Object&gt; entry) {
 * 			// TODO: Add some logic
 * 			return false;
 * 		}
 * 	});
 * 
 * long destroyedEntriesNumberForErrors = expirationController.process(&quot;errors&quot;,
 * 	new ExpirationPolicy() {
 * 
 * 		public boolean isExpired(Entry&lt;Object, Object&gt; entry) {
 * 			// TODO: Add some logic
 * 			return false;
 * 		}
 * 	});
 * 
 * expirationController.close();
 * 
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * You can get access to the other regions inside of
 * <code>isExpired(Entry&lt;Object, Object&gt;)</code> method in the following
 * way:
 * 
 * <pre>
 * new ExpirationPolicy() {
 * 
 *   public boolean isExpired(Entry&lt;Object, Object&gt; entry) {
 *     RegionService regionService = entry.getRegion().getRegionService();
 *     Region<Long, TransactionProcessingError> errorsRegion = regionService.getRegion("errors");
 * 
 *     // TODO: Add some logic
 *     return false;
 *   }
 * });
 * </pre>
 * 
 * </p>
 * 
 */
public class ExpirationController {

	private ClientCache cache;
	private ClientRegionFactory<Object, Object> clientRegionFactory;

	/**
	 * Creates the instance of ExpirationController for the specified locator.
	 * 
	 * @param locatorHost
	 *            - the host of locator
	 * @param locatorPort
	 *            - the port of locator
	 */
	public ExpirationController(String locatorHost, int locatorPort) {
		if ((locatorHost == null) || (locatorHost.trim().length() == 0)) {
			throw new IllegalArgumentException("The locator host \""
				+ locatorHost + "\" is incorrect");
		}

		if ((locatorPort < 0) || (locatorPort > 65535)) {
			throw new IllegalArgumentException("The locator port \""
				+ locatorPort
				+ "\" is incorrect. It should be in range [0, 65535].");
		}

		cache = new ClientCacheFactory().set("log-level", "warning")
			.addPoolLocator(locatorHost, locatorPort).create();

		clientRegionFactory = cache
			.createClientRegionFactory(ClientRegionShortcut.PROXY);
	}

	/**
	 * Applies the specified policy on the specified region and returns number
	 * of destroyed entries.
	 * 
	 * @param regionName
	 *            - the name of region
	 * @param policy
	 *            - the expiration policy
	 * @param packetSize
	 *            - the size of the consistently expired entries
	 * @param packetDelay
	 *            - the delay in processing after the packetSize entries,
	 *            milliseconds
	 * @return - the number of destroyed region entries
	 */
	public long process(String regionName, ExpirationPolicy policy,
		long packetSize, long packetDelay) {
		if (cache == null) {
			throw new IllegalStateException(
				"It seems that the workflow of the controller is already finished");
		}

		if ((regionName == null) || (regionName.trim().length() == 0)) {
			throw new IllegalArgumentException("The region name \""
				+ regionName + "\" is incorrect");
		}

		if (policy == null) {
			throw new IllegalArgumentException(
				"The ExpirationPolicy cannot be null");
		}

		Region<Object, Object> region = cache.getRegion(regionName);

		if (region == null) {
			region = clientRegionFactory.create(regionName);
		}

		if (region == null) {
			throw new IllegalStateException(
				"Cannot retrieve access to the region with name \""
					+ regionName + "\"");
		}

		long destroyedEntriesNumber = 0;

		DestroyedEntriesCountCollector collector = (DestroyedEntriesCountCollector) FunctionService
			.onRegion(region)
			.withArgs(new ExpirationFunctionArguments(packetSize, packetDelay))
			.withCollector(new DestroyedEntriesCountCollector())
			.execute(new ExpirationFunction(policy));

		Object result = collector.getResult();
		if (result instanceof Long) {
			destroyedEntriesNumber = (Long) result;
		}

		return destroyedEntriesNumber;
	}

	/**
	 * Applies the specified policy on the specified region and returns number
	 * of destroyed entries.
	 * 
	 * Do not use this method for processing the big region.
	 * 
	 * @param regionName
	 *            - the name of region
	 * @param policy
	 *            - the expiration policy
	 * @return - the number of destroyed region entries
	 */
	public long process(String regionName, ExpirationPolicy policy) {
		return process(regionName, policy, 1, 0);
	}

	/**
	 * Finishes the workflow of controller.
	 */
	public void close() {
		if (cache != null) {
			cache.close();
		}
	}
}
