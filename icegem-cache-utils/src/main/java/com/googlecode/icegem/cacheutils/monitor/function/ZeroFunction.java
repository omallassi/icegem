package com.googlecode.icegem.cacheutils.monitor.function;

import java.util.Properties;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;

/**
 * Just returns zero.
 */
public class ZeroFunction extends FunctionAdapter implements Declarable {

	private static final long serialVersionUID = -6448375948152121283L;

	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;

	/* The time since the first function execution when to start the hanging emulation */
	private static final long START_HANG_AFTER = 1 * MINUTE;

	/* The time since the first function execution when to stop the hanging emulation */
	private static final long STOP_HANG_AFTER = 3 * MINUTE;
	
	/* The duration of hanging inside of function */
	private static final long HANG_DURATION = 30 * MINUTE;

	/* The time of first time function execution */
	private static long firstRunTime = -1;

	public void init(Properties arg0) {
		// do nothing
	}

	public void execute(FunctionContext functionContext) {
		if (firstRunTime == -1) {
			firstRunTime = System.currentTimeMillis();
		}

		long currentTime = System.currentTimeMillis();
		long delta = currentTime - firstRunTime;
		
		//System.out.println("delta = " + delta);
		if ((delta > START_HANG_AFTER) && (delta <= STOP_HANG_AFTER)) {
			try {
				Thread.sleep(HANG_DURATION);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		
		functionContext.getResultSender().lastResult(new Integer(0));
	}

	@Override
	public String getId() {
		return this.getClass().getName();
	}

}
