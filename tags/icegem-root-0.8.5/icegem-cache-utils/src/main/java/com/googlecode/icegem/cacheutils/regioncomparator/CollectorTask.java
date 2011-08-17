package com.googlecode.icegem.cacheutils.regioncomparator;

import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class CollectorTask   implements Callable<PoolResult> {
    private Set keys;
        private Pool pool;
        private String regionName;

        CollectorTask(Set keys, Pool pool, String regionName) {
            this.keys = keys;
            this.pool = pool;
            this.regionName = regionName;
        }

        public PoolResult call() throws Exception {
            Map args = new HashMap();
            args.put("regionName", regionName);
            args.put("keys", keys);
            ResultCollector rc = FunctionService.onServers(pool)
                    .withArgs((HashMap) args)
                    .execute(new HashCodeCollectorFunction());
            return new PoolResult(pool, rc);
        }
}
