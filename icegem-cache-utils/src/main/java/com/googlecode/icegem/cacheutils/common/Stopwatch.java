package com.googlecode.icegem.cacheutils.common;

public class Stopwatch {

	private long startTime;
	private long finishTime;
	
	public Stopwatch() {
		reset();
	}
	
	public void start() {
		reset();
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		finishTime = System.currentTimeMillis();
	}
	
	public long getDuration() {
		long duration = 0;
		
		if (finishTime == -1) {
			duration = System.currentTimeMillis() - startTime;
		} else {
			duration = finishTime - startTime;
		}
		
		return duration;
	}
	
	private void reset() {
		startTime = -1;
		finishTime = -1;
	}
}
