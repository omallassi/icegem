package com.googlecode.icegem.cacheutils.regioncomparator;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;

import java.util.HashSet;
import java.util.Set;

/**
 * execute on partitioned region, to retrieve bucket keys
 *
 * first param is region name
 * second param is bucket id
 *
 * return keys in this bucket
 *
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class BucketKeysGetterFunction extends FunctionAdapter{

    @Override
    public void execute(FunctionContext functionContext) {
        String regionName = (String) ((Object[]) functionContext.getArguments())[0];
        Integer bucketId = (Integer) ((Object[]) functionContext.getArguments())[1];
        Region region = CacheFactory.getAnyInstance().getRegion(regionName);
        //todo: remove exceptions, unnecessary. skip member
        if (region == null)
            throw  new RuntimeException("there's no such region \'" + regionName + "\' on this sever");
        if (!PartitionRegionHelper.isPartitionedRegion(region))
            throw  new RuntimeException("this region \'" + regionName + "\' is not partitioned");

        Set keys = new HashSet();
        try {
            PartitionedRegion pr = (PartitionedRegion)region;
            if (pr.getDataStore().getAllLocalPrimaryBucketIds().contains(bucketId))
                keys.addAll(pr.getDataStore().getKeysLocally(bucketId));
        } catch (Exception e) {
            throw new RuntimeException("error getting keys for bucket " + bucketId, e);
        }

        functionContext.getResultSender().lastResult((HashSet) keys);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
