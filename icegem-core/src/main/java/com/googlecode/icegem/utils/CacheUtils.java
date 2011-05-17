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
    /** Field START_EMBEDDED_LOCATOR_COMMAND  */
    public static final String START_EMBEDDED_LOCATOR_COMMAND = "-startEmbeddedLocator";
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

    /**
     * Finds embedded locator command in server's arguments.
     *
     * @param args of type String[]
     * @return boolean
     */
    public static boolean startLocator(String[] args) {
        for (String arg : args) {
            if (START_EMBEDDED_LOCATOR_COMMAND.equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
