package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Set;

/**
 * User: akondratyev
 */
public class TouchEntryFunction extends FunctionAdapter implements Declarable{

    private static Logger logger = LoggerFactory.getLogger(TouchEntryFunction.class);

    public void init(Properties properties) {}

    @Override
    public boolean isHA() { return false; }

    @Override
    public boolean hasResult() { return false; }

    @Override
    public void execute(FunctionContext functionContext) {
        RegionFunctionContext context = (RegionFunctionContext) functionContext;
        Region messageRegion = context.getDataSet();
        Set keys = context.getFilter();
        logger.trace("execute on {} for keys {}", new Object[] {messageRegion.getName(), keys});
        messageRegion.getAll(keys);     //todo: it's not effective
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
