package com.googlecode.icegem.cacheutils.regioncomparator;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.internal.cache.execute.DefaultResultCollector;
import com.gemstone.gemfire.distributed.DistributedSystem;

public class RegionComparator {
    private static final Logger log = LoggerFactory
            .getLogger(RegionComparator.class);

    public List compareSingleCluster(Object[] keySetOnServer, String locators, String regionPath) {
        Properties props = new Properties();
        props.setProperty("mcast-port", "0");
        props.setProperty("locators", locators);
        props.setProperty("log-file", "comparator-member.log");
        DistributedSystem system = DistributedSystem.connect(props);
        Cache cache = new CacheFactory()
                            .set("mcast-port", "0")
                            .set("locators", locators)
                            .set("log-file", "comparator-member.log")
                            .create();

        Object[] args = new Object[2];
        args[0] = regionPath;
        args[1] = keySetOnServer;
        Execution execution = FunctionService.onMembers(system)
                .withArgs(args)
                .withCollector(new DefaultResultCollector());
        //ResultCollector rc = execution.execute("com.googlecode.gemfire.cacheutils.regioncomparator.RegionCompareFunction", true);
        ResultCollector rc = execution.execute(new RegionCompareFunction());
        List result = (List) rc.getResult();
        String localId = system.getDistributedMember().getId();
        Iterator iterator =  result.iterator();
        while (iterator.hasNext()) {
             Object[][] res = (Object[][]) iterator.next();
             if (res[3][0].equals(localId))
                iterator.remove();
        }
        cache.close();
        system.disconnect();
        return result;

    }

    public List compareDifferentClusters(Region<?, ?> region) {
        Object[] args = new Object[2];
        args[0] = region.getFullPath();
        args[1] = region.keySetOnServer().toArray();
        FunctionService.registerFunction(new RegionCompareFunction());
        Execution execution = FunctionService.onServers(region.getCache())
                .withArgs(args)
                .withCollector(new DefaultResultCollector());

        //ResultCollector rc = execution.execute("com.googlecode.gemfire.cacheutils.regioncomparator.RegionCompareFunction", true);
        ResultCollector rc = execution.execute(new RegionCompareFunction());
        return (List) rc.getResult();
    }
}