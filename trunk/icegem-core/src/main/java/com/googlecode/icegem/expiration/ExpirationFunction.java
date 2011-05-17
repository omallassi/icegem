package com.googlecode.icegem.expiration;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

/**
 * Expires the region entries according to the ExpirationPolicy specified.
 */
public class ExpirationFunction extends FunctionAdapter implements Declarable {

	private static final long serialVersionUID = -6448375948152121283L;

	private ExpirationPolicy policy;

	/**
	 * Creates the instance of the ExpirationFunction and configures it with
	 * ExpirationPolicy.
	 * 
	 * @param policy
	 *            - the expiration policy
	 */
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

			for (Entry<Object, Object> entry : entrySet) {
				if (entry instanceof Region.Entry) {
					Region.Entry<Object, Object> regionEntry = (Region.Entry<Object, Object>) entry;
					if ((policy != null) && policy.isExpired(regionEntry)) {
						region.destroy(entry.getKey());
						destroyedEntriesCount++;
					}
				}
			}
		}

		functionContext.getResultSender().lastResult(destroyedEntriesCount);
	}

	@Override
	public String getId() {
		return this.getClass().getName();
	}
}
