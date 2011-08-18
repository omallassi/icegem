package com.googlecode.icegem.cacheutils.common;

/**
 * 
 */
public class Stopwatch {
    /** */
    private long startTime;
    
    /** */
    private long finishTime;

    /**
     * Creates new stopwatch.
     */
    public Stopwatch() {
	reset();
    }

    /**
     * Starts measuring.
     */
    public void start() {
	reset();
	
	startTime = System.currentTimeMillis();
    }

    /**
     * Stops measuring.
     */
    public void stop() {
	finishTime = System.currentTimeMillis();
    }

    /**
     * @return - Measured duration in milliseconds. 
     */
    public long getDuration() {
	long duration = 0;

	if (finishTime == -1) {
	    duration = System.currentTimeMillis() - startTime;
	} else {
	    duration = finishTime - startTime;
	}

	return duration;
    }

    /**
     * Resets stopwatch.
     */
    private void reset() {
	startTime = -1;
	finishTime = -1;
    }
}
