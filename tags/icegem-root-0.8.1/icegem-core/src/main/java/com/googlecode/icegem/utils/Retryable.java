/**
 * 
 */
package com.googlecode.icegem.utils;

/**
 * is an operation that can be retried with exponentional back off.
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
public interface Retryable<T> {

	/**
	 * executes the operation.
	 * 
	 * @return the return value of operation.
	 * @throws OperationRequireRetryException when operation have to be retried.
	 * @throws InterruptedException if operation has been interrupted.
	 */
	T execute() throws OperationRequireRetryException, InterruptedException;
	
}
