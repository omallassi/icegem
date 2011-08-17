package com.googlecode.icegem.utils;

import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;

/**
 * @author Renat Akhmerov.
 */
public class RegionSizeResultCollector implements ResultCollector<Integer, Integer> {
    /** Collected region size. */
    private int size;

    /** Mutex. */
    private final Object mux = new Object();

    /** Done flag. */
    private boolean done;

    @Override
    public void addResult(DistributedMember memberID, Integer singleRes) {
	if (singleRes != null)
	    size += singleRes;
    }

    @Override
    public void clearResults() {
	size = 0;
    }

    @Override
    public void endResults() {
	synchronized (mux) {
	    done = true;

	    mux.notifyAll();
	}
    }

    @Override
    public Integer getResult() throws FunctionException {
	try {
	    return getResult(0, null);
	} catch (InterruptedException e) {
	    // Should never happen.
	    throw new FunctionException(e);
	}
    }

    @Override
    public Integer getResult(long timeout, TimeUnit unit) throws FunctionException, InterruptedException {
	if (timeout > 0 && unit == null)
	    throw new IllegalArgumentException("Parameter unit cannot be nul if timeout > 0");

	synchronized (mux) {
	    while (!done) {
		if (timeout > 0) {
		    unit.timedWait(mux, timeout);
		} else {
		    mux.wait();
		}
	    }
	}

	return size;
    }
}
