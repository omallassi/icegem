package com.googlecode.icegem.utils;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.googlecode.icegem.utils.function.ClearPartitionedRegionFunction;

/**
 * Help class for common operations with regions.
 *
 * @author Andrey Stepanov aka standy
 */
public class RegionUtils {
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
