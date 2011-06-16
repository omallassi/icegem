/**
 * 
 */
package com.googlecode.icegem.utils;

/**
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
@SuppressWarnings("serial")
public class OperationRequireRetryException extends Exception {

	public OperationRequireRetryException() {
		super();
	}

	public OperationRequireRetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public OperationRequireRetryException(String message) {
		super(message);
	}

	public OperationRequireRetryException(Throwable cause) {
		super(cause);
	}

}
