/**
 * 
 */
package com.googlecode.icegem.utils;

/**
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 */
@SuppressWarnings("serial")
public class OperationRequireRetryException extends Exception {
    /**
     * 
     */
    public OperationRequireRetryException() {
	super();
    }

    /**
     * @param message Exception message.
     * @param cause Cause
     */
    public OperationRequireRetryException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * @param message Exception message.
     */
    public OperationRequireRetryException(String message) {
	super(message);
    }

    /**
     * @param cause Cause.
     */
    public OperationRequireRetryException(Throwable cause) {
	super(cause);
    }
}
