package com.googlecode.icegem.utils;

import java.io.Serializable;

import com.gemstone.gemfire.cache.DataPolicy;
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

    @Override
    public void execute(FunctionContext ctx) {
	ResultSender<Serializable> sndr = ctx.getResultSender();

	if (!(ctx instanceof RegionFunctionContext))
	    sndr.sendException(new FunctionException("Function context must be of type RegionFunctionContext."));

	RegionFunctionContext regionCtx = (RegionFunctionContext) ctx;

	Region<Object, Object> region = regionCtx.getDataSet();

	DataPolicy policy = region.getAttributes().getDataPolicy();

	Region<Object, Object> localRegion = region;

	if (policy == DataPolicy.PARTITION || policy == DataPolicy.PERSISTENT_PARTITION) {
	    localRegion = PartitionRegionHelper.getLocalData(region);
	}

	sndr.lastResult(localRegion.size());
    }
}
