package com.googlecode.icegem.expiration;

import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;

/**
 * The wrapper of ExpirationController to simplify its use with Spring
 */
public class ExpirationControllerSpringWrapper {

	// Wrapped ExpirationController
	private ExpirationController controller;

	// Parameters of ExpirationController#process() method
	private String regionName;
	private ExpirationPolicy policy;
	private long packetSize;
	private long packetDelay;

	/**
	 * Creates an instance of the wrapper.
	 * 
	 * @param cache
	 *            - the client cache configured outside
	 * @param clientRegionFactory
	 *            - the client cache factory configured outside
	 */
	public ExpirationControllerSpringWrapper(ClientCache cache,
		ClientRegionFactory<Object, Object> clientRegionFactory) {

		controller = new ExpirationController(cache, clientRegionFactory);
	}

	/**
	 * Sets the regionName parameter of ExpirationController#process() method
	 * 
	 * @param regionName
	 *            - the region name
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/**
	 * Sets the policy parameter of ExpirationController#process() method
	 * 
	 * @param policy
	 *            - the ExpirationPolicy instance configured outside
	 */
	public void setPolicy(ExpirationPolicy policy) {
		this.policy = policy;
	}

	/**
	 * Sets the packetSize parameter of ExpirationController#process() method
	 * 
	 * @param packetSize
	 *            - the size of entries packet
	 */
	public void setPacketSize(long packetSize) {
		this.packetSize = packetSize;
	}

	/**
	 * Sets the packetDelay parameter of ExpirationController#process() method
	 * 
	 * @param packetDelay
	 *            - the delay after processing each packet of entries
	 */
	public void setPacketDelay(long packetDelay) {
		this.packetDelay = packetDelay;
	}

	/**
	 * Runs the ExpirationController#process() method with the previously
	 * specified parameters
	 */
	public void process() {
		controller.process(regionName, policy, packetSize, packetDelay);
	}

}
