package com.googlecode.icegem.cacheutils.regioncomparator;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;

import java.util.*;

/**
 * extract keys from region:
 *  for partitioned by buckets
 *  for replicated by batch size
 *
* User: Artem Kondratyev, e-mail: kondratevae@gmail.com
*/
class KeyExtractor {

    private String regionName;
    private Pool sourcePool;
    private boolean isPartitioned;
    private int totalBatches;
    private int currentBatch = 0;
    private List<Set> keysBatch;

    KeyExtractor(String regionName, Pool sourcePool, boolean isPartitioned, int totalBatches) {
        assert regionName != null;
        assert sourcePool != null;
        this.regionName = regionName;
        this.sourcePool = sourcePool;
        this.isPartitioned = isPartitioned;
        this.totalBatches = totalBatches;
        keysBatch = new ArrayList<Set>();
        if (!isPartitioned) {
            Map args = new HashMap();
            args.put("regionName", regionName);
            ResultCollector resultCollector = FunctionService.onServer(sourcePool)
                    .withArgs((HashMap) args)
                    .execute(new GetKeysFunction());
            for(Object keys: (List) resultCollector.getResult()) {
                keysBatch.add((Set) keys);
            }
        }
    }

    public boolean hasKeys() {
        return currentBatch <= totalBatches;
    }

    public Set getNextKeysBatch() {
        Set result = new HashSet();
        if (isPartitioned) {
            Map args = new HashMap();
            args.put("regionName", regionName);
            while (result.isEmpty() && hasKeys()) {     //todo: less then configured buckets count                                 //iterate over empty buckets
                args.put("bucket", currentBatch);
                currentBatch++;
                List keySearchResult = (List) FunctionService.onServers(sourcePool)
                    .withArgs((HashMap) args)
                    .execute(new GetKeysFunction())
                    .getResult();
                for (Object keySearchResultForMember: keySearchResult) {
                    result.addAll((Set)keySearchResultForMember);
            }
            }
            System.out.println("found keys for bucket " + currentBatch);
            return result;
        }

        return keysBatch.get(currentBatch++);
    }
}
