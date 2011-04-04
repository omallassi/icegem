package com.griddynamics.icegem.cacheutils.regioncomparator;

import java.io.Serializable;
import java.util.*;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;

public class RegionCompareFunction extends FunctionAdapter {
    private static final long serialVersionUID = 4569716549835836666L;

    public void execute(FunctionContext fc) {
        try {
            Object[] argsArray = (Object[]) fc.getArguments();
            String regionPath = (String) argsArray[0];
            Object[] keySetOnServer = (Object[]) argsArray[1];
            Cache cache = CacheFactory.getAnyInstance();
            Region region = cache.getRegion(regionPath);
            if (region != null) {
                Map distributedEntriesMap = new HashMap(region.getAll(Arrays.asList(keySetOnServer)));
                Object[] localEntries = region.entrySet().toArray();
                Map localEntriesMap = new HashMap();

                for (Object entry : localEntries) {
                    localEntriesMap.put(((Map.Entry) entry).getKey(), ((Map.Entry) entry).getValue());
                }
                Object[][] result = compareRegions(distributedEntriesMap, localEntriesMap, region);
                fc.getResultSender().lastResult((Serializable) result);
            } else {
                Object[][] result = new Object[4][1];
                result[0][0] = null;
                result[1][0] = null;
                result[2][0] = null;
                result[3][0] = cache.getDistributedSystem().getDistributedMember().getId();
                fc.getResultSender().lastResult((Serializable) result);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            Object[][] result = new Object[4][1];
            result[0][0] = null;
            result[1][0] = null;
            result[2][0] = null;
            result[3][0] = ex.getMessage() + ex.getCause().getMessage();
            fc.getResultSender().lastResult((Serializable) result);
        }

    }

    private Object[][] compareRegions(Map distributedEntries, Map localEntries, Region region) {
        if (distributedEntries.equals(localEntries)) {
            Object[][] result = new Object[4][1];
            result[0][0] = null;
            result[1][0] = null;
            result[2][0] = null;
            result[3][0] = "equals";//region.getCache().getDistributedSystem().getDistributedMember().getId();
            return result;
        }
        Set missing = new HashSet(distributedEntries.keySet());
        missing.removeAll(localEntries.keySet());

        Set extra = new HashSet(localEntries.keySet());
        extra.removeAll(distributedEntries.keySet());

        Set different = new HashSet();

        distributedEntries.keySet().removeAll(missing);
        localEntries.keySet().removeAll(extra);

        for (Object key : distributedEntries.keySet()) {
            if (!distributedEntries.get(key).equals(localEntries.get(key)))
                different.add(key);

        }
        Object[][] result = new Object[4][1];
        result[0][0] = missing.toArray();
        result[1][0] = extra.toArray();
        result[2][0] = different.toArray();
        result[3][0] = region.getCache().getDistributedSystem().getDistributedMember().getId();
        return result;
    }

    public String getId() {
        return getClass().getName();
    }

}
