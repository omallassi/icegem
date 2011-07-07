package com.googlecode.icegem.cacheutils.regioncomparator;

import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.execute.ResultCollector;

/**
 * User: Artem Kondratev kondratevae@gmail.com
 */
public class PoolResult {
    private Pool pool;
    private ResultCollector resultCollector;

    public PoolResult(Pool pool, ResultCollector resultCollector) {
        this.pool = pool;
        this.resultCollector = resultCollector;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public ResultCollector getResultCollector() {
        return resultCollector;
    }

    public void setResultCollector(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }
}
