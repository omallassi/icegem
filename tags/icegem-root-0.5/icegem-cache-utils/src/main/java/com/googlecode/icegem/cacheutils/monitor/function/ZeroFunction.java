package com.googlecode.icegem.cacheutils.monitor.function;

import java.util.Properties;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;

/**
 * Just returns zero.
 *
 */
public class ZeroFunction extends FunctionAdapter implements Declarable {

	private static final long serialVersionUID = -6448375948152121283L;

	public void init(Properties arg0) {
		// do nothing
	}

	public void execute(FunctionContext functionContext) {
		functionContext.getResultSender().lastResult(new Integer(0));
	}

	@Override
	public String getId() {
		return this.getClass().getName();
	}

}
