package com.googlecode.icegem.expiration;

import java.io.Serializable;

import com.gemstone.gemfire.cache.Region.Entry;

public interface ExpirationPolicy extends Serializable {

	boolean isExpired(Entry<Object, Object> entry);
	
}
