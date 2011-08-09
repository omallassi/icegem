package com.googlecode.icegem.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.googlecode.icegem.utils.function.ClearPartitionedRegionFunction;

/**
 * Help class for common operations with regions.
 *
 * @author Andrey Stepanov aka standy
 */
public class CacheUtils {
    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    private static long BACKOFF_BASE = 10L;

    /**
	 * Limits query results.
	 * 
	 * @param queryString
	 *            of type String
	 * @return String
	 */
	public static String addQueryLimit(String queryString, int queryLimit) {
		int limitIndex = queryString.lastIndexOf("limit");
		if (limitIndex == -1) {
			limitIndex = queryString.lastIndexOf("LIMIT");
		}
		if (limitIndex == -1) {
			return queryString + " LIMIT " + (queryLimit + 1);
		}
		int limitNumber = Integer.parseInt(queryString
				.substring(limitIndex + 5).trim());
		return (limitNumber > queryLimit) ? queryString
				.substring(0, limitIndex) + " LIMIT " + (queryLimit + 1)
				: queryString;
	}

	/**
     * Clears all types of regions.
     * This method can clean both types of regions (REPLICATED, PARTITIONED).
     * It can be used both on client and server side.
     *
     * Note: if this method will be used from client that configured as CACHING_PROXY:
     * clearing of PARTITIONED regions:
     *  - if client's data should be cleared together with server's data, client interest should be registered on entries changes;
     * clearing of REPLICATED regions:
     *  - if client's data should be cleared together with server's data, this method should be invoked from the client together with region.localClear() method.
     *
     * @param region
     *            partitioned region
     */
    public static void clearRegion(Region region) {
        ClearPartitionedRegionFunction cleaner = new ClearPartitionedRegionFunction();
        FunctionService.registerFunction(cleaner);
        ResultCollector rc = FunctionService.onRegion(region)
                .withArgs(region.getName()).execute(cleaner);
        rc.getResult();
    }
    
    /**
     * Retries passed operation with random exponential back off delay.
     * @param <T> Type of returned value.
     * @param runnable the operation.
     * @param maxRetries the maximum number of retries.
     * @return the value returned by operation
     * @throws OperationRetryFailedException
     * @throws InterruptedException
     */
    public static <T> T retryWithExponentialBackoff(Retryable<T> runnable,
                                                    int maxRetries) throws InterruptedException, OperationRetryFailedException {
        int retry = 0;
        while (retry < maxRetries) {
            retry++;
            try {
                return runnable.execute();
            } catch (OperationRequireRetryException e) {
            } catch (InterruptedException e) {
                throw e;
            }
            if (retry > 1) {
                long delay = (long) ((BACKOFF_BASE << retry) * Math.random());
                logger.debug("Operation requested retry. Sleep for {} millis", delay);
                Thread.sleep(delay);
            }
        }
        throw new OperationRetryFailedException("Maximum number of operation retries reached");
    }
}