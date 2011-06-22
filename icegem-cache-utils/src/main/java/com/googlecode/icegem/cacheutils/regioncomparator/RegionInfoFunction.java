package com.googlecode.icegem.cacheutils.regioncomparator;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class RegionInfoFunction extends FunctionAdapter {

    private static int BATCH_SIZE = 3;   //todo: for replicated region. see: GetKeysFunction
    private static final long serialVersionUID = -7733341987189336659L;

    @Override
    public void execute(FunctionContext functionContext) {
        String regionName = (String) functionContext.getArguments();
        Region region = CacheFactory.getAnyInstance().getRegion(regionName);
        if (region == null) {
            System.out.println("region " + regionName + " doesn't exist");
            functionContext.getResultSender().lastResult(new HashMap());
        }
        /*if (region == null)
            throw new NullPointerException("there's no such region \'" + regionName + "\' on this server");*/

        Map<String, Object> regionInfo = new HashMap<String, Object>();
        boolean isPartitioned = false;
        if (PartitionRegionHelper.isPartitionedRegion(region))
            isPartitioned = true;
        regionInfo.put("isPartitioned", isPartitioned);
        regionInfo.put("id", CacheFactory.getAnyInstance().getDistributedSystem().getDistributedMember().getId());

        if (isPartitioned) {
            /*int totalNumBuckets = PartitionRegionHelper.getPartitionRegionInfo(region).getCreatedBucketCount();*/
            int totalNumBuckets = PartitionRegionHelper.getPartitionRegionInfo(region).getConfiguredBucketCount();
            regionInfo.put("totalNumBuckets", totalNumBuckets);
        } else {
            if (region.keySet().size() > 0)
                regionInfo.put("totalNumBuckets", region.keySet().size() / BATCH_SIZE + 1);
            else
                regionInfo.put("totalNumBuckets", 0);
        }
        functionContext.getResultSender().lastResult((HashMap) regionInfo);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}

