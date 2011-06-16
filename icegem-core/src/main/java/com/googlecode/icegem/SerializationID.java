/**
 * 
 */
package com.googlecode.icegem;

import com.googlecode.icegem.barrier.MessageHolder;

/**
 * is a collection of DataSerializable class IDs used by ICEGEM library.
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
public interface SerializationID {
	/**
	 * Base offset of serialization IDs.
	 */
	int BASE = 16000;
	
	/**
	 * {@link MessageHolder}
	 */
	int MESSAGE_HOLDER_ID = BASE + 1;
	
	
}
