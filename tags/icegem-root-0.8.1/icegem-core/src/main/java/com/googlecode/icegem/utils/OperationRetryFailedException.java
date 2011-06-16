/**
 * 
 */
package com.googlecode.icegem.utils;

/**
 * is thrown when maximum number of retries exceeded.
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
@SuppressWarnings("serial")
public class OperationRetryFailedException extends Exception {

	public OperationRetryFailedException() {
		super();
	}

	public OperationRetryFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public OperationRetryFailedException(String message) {
		super(message);
	}

	public OperationRetryFailedException(Throwable cause) {
		super(cause);
	}

}
