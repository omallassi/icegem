package com.googlecode.icegem.cacheutils.comparator;

import java.io.Serializable;
import java.util.Properties;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.googlecode.icegem.cacheutils.common.Utils;

/**
 * Just returns zero.
 */
public class HashcodeFunction extends FunctionAdapter implements Declarable {

	private static final long serialVersionUID = -6448375948152121283L;

	private static final int BASE_MULTIPLIER = 1;

	public void init(Properties arg0) {
		// do nothing
	}

	public void execute(FunctionContext functionContext) {
		ResultSender<Serializable> resultSender = functionContext
			.getResultSender();

		try {
			RegionFunctionContext context = (RegionFunctionContext) functionContext;

			HashcodeFunctionArguments arguments = (HashcodeFunctionArguments) context.getArguments();
			
			Utils.registerClasses(arguments.getPackages());

			Region<?, ?> region = context.getDataSet();

			if (PartitionRegionHelper.isPartitionedRegion(region)) {
				region = PartitionRegionHelper.getLocalData(region);
			}

			long hashcode = calculateHashcode(region, arguments.getLoadFactor());

			resultSender.lastResult(hashcode);
		} catch (Throwable t) {
			resultSender.sendException(t);
		}
	}

	public long calculateHashcode(Region<?, ?> region, Integer loadFactor) {
		long hashcode = 0;

		long lastWakeUpTime = System.currentTimeMillis();
		for (Object key : region.keySet()) {
			Object value = region.get(key);

			int keyHashcode = key.hashCode();
			int valueHashcode = value.hashCode();

			long entryHashcode = (((long) keyHashcode) << 32) + valueHashcode;

			hashcode += entryHashcode;

			lastWakeUpTime = sleep(lastWakeUpTime, loadFactor);
		}

		return hashcode;
	}

	private long sleep(long lastWakeUpTime, Integer loadFactor) {

		long nextSleepTime = lastWakeUpTime + BASE_MULTIPLIER * loadFactor;
		long currentTime = System.currentTimeMillis();

		boolean hasSleep = false;
		if (currentTime > nextSleepTime) {
			try {
				Thread.sleep(BASE_MULTIPLIER * (100 - loadFactor));
			} catch (InterruptedException e) {
			}
			
			hasSleep = true;
		}

		long newLastWakeUpTime;
		if (hasSleep) {
			newLastWakeUpTime = System.currentTimeMillis();
		} else {
			newLastWakeUpTime = lastWakeUpTime;
		}

		return newLastWakeUpTime;
	}

	@Override
	public String getId() {
		return this.getClass().getName();
	}

	@Override
	public boolean isHA() {
		return false;
	}
}
