package com.googlecode.icegem.expiration;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

public class ExpirationFunction extends FunctionAdapter implements Declarable {

	private static final long serialVersionUID = -6448375948152121283L;

	private ExpirationPolicy policy;

	public ExpirationFunction(ExpirationPolicy policy) {
		this.policy = policy;
	}

	public void init(Properties arg0) {
		// do nothing
	}

	public void execute(FunctionContext functionContext) {
		long destroyedEntriesCount = 0;

		if (functionContext instanceof RegionFunctionContext) {
			RegionFunctionContext context = (RegionFunctionContext) functionContext;
			Region<Object, Object> region = PartitionRegionHelper
				.getLocalDataForContext(context);

			Set<Entry<Object, Object>> entrySet = region.entrySet();
			Set<Entry<Object, Object>> expiredEntrySet = new HashSet<Entry<Object, Object>>();

			for (Entry<Object, Object> entry : entrySet) {
				if (entry instanceof Region.Entry) {
					Region.Entry<Object, Object> regionEntry = (Region.Entry<Object, Object>) entry;
					if ((policy != null) && policy.isExpired(regionEntry)) {
						expiredEntrySet.add(regionEntry);
					}
				}
			}

			for (Entry<Object, Object> entry : expiredEntrySet) {
				region.destroy(entry.getKey());
				destroyedEntriesCount++;
			}
		}

		functionContext.getResultSender().lastResult(destroyedEntriesCount);
	}

	@Override
	public String getId() {
		return this.getClass().getName();
	}
}
