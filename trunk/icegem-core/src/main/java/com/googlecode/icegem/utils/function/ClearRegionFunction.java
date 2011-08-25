package com.googlecode.icegem.utils.function;

import com.gemstone.gemfire.admin.RegionNotFoundException;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

import java.util.Set;

/**
 * Function for clearing regions of different types.
 * @see com.googlecode.icegem.utils.CacheUtils for more details.
 *
 * @author Andrey Stepanov aka standy
 */
public class ClearRegionFunction extends FunctionAdapter {
    private final static String FUNCTION_ID = ClearRegionFunction.class.getName();

    @SuppressWarnings({ "ThrowableInstanceNeverThrown" })
    @Override
    public void execute(FunctionContext functionContext) {
	ResultSender<Boolean> rs = functionContext.getResultSender();
	Object arg = functionContext.getArguments();

	if (!(arg instanceof String)) {
	    rs.sendException(new IllegalStateException("Function parameter must be instance of String.class"));
	    
	    return;
	}

	String regionName = (String) arg;

	Region<Object, Object> region = CacheFactory.getAnyInstance().getRegion(regionName);
	
	if (region == null) {
	    rs.sendException(new RegionNotFoundException("Region '" + regionName + "' does not exist on this member"));
	    
	    return;
	}

	if (region.getAttributes().getDataPolicy().withPartitioning()) {
	    Set<Object> keys = PartitionRegionHelper.getLocalPrimaryData(region).keySet();
	    
	    for (Object key : keys) {
		region.destroy(key);
	    }
	} else {
	    region.clear();
	}
	
	rs.lastResult(true);
    }

    @Override
    public String getId() {
	return FUNCTION_ID;
    }
}
