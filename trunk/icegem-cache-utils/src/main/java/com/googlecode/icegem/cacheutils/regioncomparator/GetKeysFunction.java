package com.googlecode.icegem.cacheutils.regioncomparator;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.internal.cache.ForceReattemptException;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;
import com.gemstone.gemfire.internal.cache.PartitionedRegionHelper;
import com.gemstone.gemfire.internal.cache.partitioned.PRLocallyDestroyedException;

import java.util.*;

/**
 * return keys for a particular bucket or all keys for replicated region
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class GetKeysFunction extends FunctionAdapter {

    private static final long serialVersionUID = -1467135292875589062L;
    private static int BATCH_SIZE = 3;                  //todo: how to customize this parameter. also it exists in regioninfofunction. ugly.

    @Override
    public void execute(FunctionContext functionContext) {
        Map<String, Object> args = (HashMap) functionContext.getArguments();
        String regionName = (String) args.get("regionName");
        Region region = CacheFactory.getAnyInstance().getRegion(regionName);
        if (region == null) {
            functionContext.getResultSender().lastResult(new HashSet());
            return;
        }

        int bucket = 0;
        if (PartitionRegionHelper.isPartitionedRegion(region)) {
            bucket = (Integer) args.get("bucket");                                              //todo: NPE if actual region is different from parameter (replicate vs partition)
            PartitionedRegion pr = (PartitionedRegion) region;
            Set keys = new HashSet();
            if (pr.getDataStore().getAllLocalPrimaryBucketIds().contains(bucket)) {
                try {
                    keys.addAll(pr.getDataStore().getKeysLocally(bucket));
                } catch (Exception e) {
                    throw new RuntimeException("error getting local keys for bucket " + bucket, e);
                }
            }
            functionContext.getResultSender().lastResult((HashSet) keys);
            return;
        }

        //todo: it's ugly. better: to ask a particular batch of keys (between oldBatch upper bound and nextBatch lower bound)
        Set keys = region.keySet();
        Iterator iterator = keys.iterator();
        Set keysBatch = new HashSet(BATCH_SIZE);
        while(iterator.hasNext()) {
            keysBatch.add(iterator.next());
            if ((keysBatch.size() + 1) % BATCH_SIZE  == 0) {
                functionContext.getResultSender().sendResult((HashSet) keysBatch);
                keysBatch = new HashSet(BATCH_SIZE);
            }
        }
        functionContext.getResultSender().lastResult((HashSet) keysBatch);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
