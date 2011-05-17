package com.googlecode.icegem.expiration;

import java.io.Serializable;

import com.gemstone.gemfire.cache.Region.Entry;

/**
 * Interface for the expiration policy (strategy).
 */
public interface ExpirationPolicy extends Serializable {

	/**
	 * Defines whether the Region.Entry is expired.
	 * 
	 * @param entry
	 *            - the region entry
	 * @return - true in case of it is expired, false otherwise
	 */
	boolean isExpired(Entry<Object, Object> entry);

}
