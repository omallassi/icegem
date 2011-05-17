package com.googlecode.icegem.expiration;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;

/**
 * Collects the partial destroyedEntriesCount values and sums them
 */
public class DestroyedEntriesCountCollector implements
	ResultCollector<Serializable, Serializable> {
	private Semaphore lock = new Semaphore(1);
	private long destroyedEntriesCount;

	public void addResult(DistributedMember member, Serializable value) {
		try {
			lock.acquire();

			if (value instanceof Long) {
				long partialCount = (Long) value;
				destroyedEntriesCount += partialCount;
			}

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}

	}

	public void clearResults() {
		try {
			lock.acquire();

			destroyedEntriesCount = 0;

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}
	}

	public void endResults() {
	}

	public Serializable getResult() throws FunctionException {
		try {
			lock.acquire();

			return destroyedEntriesCount;

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}
	}

	public Serializable getResult(long timeout, TimeUnit timeUnit)
		throws FunctionException, InterruptedException {
		try {
			if (!lock.tryAcquire(timeout, timeUnit)) {
				throw new FunctionException("Timeout during the lock acquiring");
			}

			return destroyedEntriesCount;

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}
	}

}
