package com.googlecode.icegem.cacheutils.comparator;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;

public class HashcodeResultCollector implements
	ResultCollector<Serializable, Serializable> {

	private Semaphore lock = new Semaphore(1);
	private long hashcode;

	public void addResult(DistributedMember member, Serializable value) {
		try {
			lock.acquire();

			if (value instanceof Long) {
				long partialHashcode = (Long) value;
				hashcode += partialHashcode;
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

			hashcode = 0;

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

			return hashcode;

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

			return hashcode;

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}
	}
}
