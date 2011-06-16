/**
 * 
 */
package com.googlecode.icegem.barrier;

import java.io.Serializable;

/**
 * is an abstract message destination.
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
public interface MessageDestination {

	public void send(String barrierName, Serializable message);
}
