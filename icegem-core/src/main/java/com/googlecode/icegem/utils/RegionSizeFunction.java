package com.googlecode.icegem.utils;

import java.io.Serializable;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

/**
 * 
 * @author Renat Akhmerov.
 */
public class RegionSizeFunction extends FunctionAdapter {
    /** Serial version UID. */
    private static final long serialVersionUID = 2939897587860693898L;

    @Override
    public String getId() {
	return RegionSizeFunction.class.getName();
    }

    /**
     * NOTE: This method should return true so that the function would not be sent
     * to nodes which don't have any primary data.   
     */
    @Override
    public boolean optimizeForWrite() {
	return true;
    }
    
    @Override
    public void execute(FunctionContext ctx) {
	ResultSender<Serializable> sndr = ctx.getResultSender();

	if (!(ctx instanceof RegionFunctionContext)) {
	    sndr.sendException(new FunctionException("Function context must be of type RegionFunctionContext."));
	    
	    return;
	}
	    
	RegionFunctionContext regionCtx = (RegionFunctionContext) ctx;

	Region<Object, Object> region = regionCtx.getDataSet();

	Region<Object, Object> localRegion = region;

	if (region.getAttributes().getDataPolicy().withPartitioning()) {
	    localRegion = PartitionRegionHelper.getLocalPrimaryData(region);
	}

	sndr.lastResult(localRegion.size());
    }
}
