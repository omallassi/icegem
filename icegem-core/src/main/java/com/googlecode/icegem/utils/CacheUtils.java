package com.googlecode.icegem.utils;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.googlecode.icegem.utils.function.ClearPartitionedRegionFunction;

/**
 * Help class for common operations with cache and regions that are used in tests.
 *
 * @author Andrey Stepanov aka standy
 */
public class CacheUtils {
    // TODO: Do not use!
    /**
     * Clears partitioned region.
     *
     * @param region partitioned region
     */
    public static void clearPartitionedRegion(Region region) {
        ClearPartitionedRegionFunction cleaner = new ClearPartitionedRegionFunction();
        FunctionService.registerFunction(cleaner);
        ResultCollector rc = FunctionService.onRegion(region)
                .withArgs(region.getName())
                .execute(cleaner);
        rc.getResult();
    }
}
