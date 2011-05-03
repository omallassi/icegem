package com.googlecode.icegem.core.functions.bucketoriented.common.functions;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.cache.query.*;
import com.gemstone.gemfire.cache.query.internal.DefaultQuery;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;
import com.gemstone.gemfire.internal.cache.PartitionedRegionHelper;

import java.io.Serializable;
import java.util.*;

/**
 * Function for retrieving data from a specified via filter keys set of buckets.
 *
 * @author Andrey Stepanov aka standy
 */
public class BucketOrientedFunction extends FunctionAdapter {
    /** Field FUNCTION_ID  */
    private final static String FUNCTION_ID = BucketOrientedFunction.class.getName();

    @Override
    public void execute(FunctionContext functionContext) {
        ResultSender<Result> rs = functionContext.getResultSender();
        RegionFunctionContext fc = (RegionFunctionContext) functionContext;

        // Only this method of getting data returns region with data from buckets that contain filter keys.
        Region<Object, Object> region = PartitionRegionHelper.getLocalDataForContext(fc);

        // If you want to know bucket id that contains specified key, you should get a PartitionRegion class.
        Cache cache = CacheFactory.getAnyInstance();
        PartitionedRegion partitionedRegion = PartitionedRegionHelper.getPartitionedRegion(region.getName(), cache);

        Set<Integer> bucketIds = new HashSet<Integer>();

        for (Object o : fc.getFilter()) {
            if (region.containsKey(o)) {
                int bucketId = partitionedRegion.getKeyInfo(region.get(o)).getBucketId();
                System.out.println("This cache server contains key = " + o + " in bucket with id = " + bucketId);
                bucketIds.add(bucketId);
            } else {
                System.out.println("This cache server doesn't contain key = " + o);
            }
        }

        // Prepare results of function.
        Map<Integer, Set<Object>> valuesByBucket = new HashMap<Integer, Set<Object>>(bucketIds.size());
        for (Integer bucketId : bucketIds) {
            valuesByBucket.put(bucketId, new HashSet<Object>(region.keySet()));
        }

        // We can also execute queries on a specified set of buckets.
        String queryString = "SELECT * FROM /data";
        Query query = cache.getQueryService().newQuery(queryString);
        try {
            // Using undocumented API of GemFire Enterprise we can execute OQL query on the set of buckets.
            // BUT: The last parameter of this function should be set of filter keys instead of set of bucket ids.
            Object results = partitionedRegion.executeQuery((DefaultQuery) query, new Object[0], fc.getFilter());
            System.out.println("Total number of entries on this cache server that satisfy filter keys: " + results.toString());
        } catch (FunctionDomainException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TypeMismatchException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NameResolutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryInvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        rs.lastResult(new Result(valuesByBucket));
    }

    /**
     * Method getId returns the id of this BucketOrientedFunction object.
     *
     * @return the id (type String) of this BucketOrientedFunction object.
     */
    @Override
    public String getId() {
        return FUNCTION_ID;
    }

    /**
     * If you will use redundancy for partitioned region than
     * GemFire will send this function to those members that contains primary or redundant copy of bucket(s).
     * In some cases it can reduce number of members that will execute this function.
     * But if you want to send this function only to those members that store primary copy of bucket,
     * you must enable a function option "optimizeForWrite".
     *
     * See forum link http://forums.gemstone.com/viewtopic.php?f=3&t=496&hilit=bucket+Id&sid=f3b823b748bb253e5019e489c8480fbd for details
     *
     * @return boolean
     */
    @Override
    public boolean optimizeForWrite() {
        return true;
    }

    /**
     * Help class for storing function results (Map<bucketId,Set<keyId>>).
     *
     * @author Andrey Stepanov aka standy
     */
    public static class Result implements Serializable {
        /** Field results  */
        private Map<Integer, Set<Object>> results;

        /**
         * Constructor Result creates a new Result instance.
         *
         * @param results of type Map<Integer, Set<Object>>
         */
        public Result(Map<Integer, Set<Object>> results) {
            this.results = results;
        }

        /**
         * Method getResults returns the results of this Result object.
         *
         * @return the results (type Map<Integer, Set<Object>>) of this Result object.
         */
        public Map<Integer, Set<Object>> getResults() {
            return results;
        }
    }
}
