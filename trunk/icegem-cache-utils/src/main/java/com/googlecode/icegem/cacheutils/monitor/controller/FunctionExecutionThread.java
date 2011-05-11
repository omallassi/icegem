package com.googlecode.icegem.cacheutils.monitor.controller;

import java.util.List;

import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.googlecode.icegem.cacheutils.monitor.function.ZeroFunction;

public class FunctionExecutionThread extends Thread {
	private Pool pool;
	private int zero = -1;

	public FunctionExecutionThread(Pool pool) {
		this.pool = pool;
	}

	@Override
	public void run() {
		try {
			zero = executeZeroFunction(pool);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Executes the "zero" function using pool for the concrete server. 
	 * 
	 * @param pool
	 *            - the pool for the concrete server
	 * @return 0 in case of function executed without problems, -1 otherwise
	 * @throws FunctionException
	 * @throws InterruptedException
	 */
	private int executeZeroFunction(Pool pool) throws FunctionException,
		InterruptedException {
		int result = -1;

		ResultCollector<?, ?> collector = FunctionService.onServer(pool)
			.execute(new ZeroFunction());

		List<?> functionResult = (List<?>) collector.getResult();
		if ((functionResult != null) && (functionResult.size() == 1)
			&& (functionResult.get(0) instanceof Integer)) {
			result = (Integer) functionResult.get(0);
		}

		return result;
	}

	public int getZero() {
		return zero;
	}
}
