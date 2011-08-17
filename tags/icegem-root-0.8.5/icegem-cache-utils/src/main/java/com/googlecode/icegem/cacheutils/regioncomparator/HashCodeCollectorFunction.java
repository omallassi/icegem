package com.googlecode.icegem.cacheutils.regioncomparator;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.monitor.StringMonitor;
import java.util.*;

/**
 * return 1) member id
 *        2) if region is absent, empty hash map
 *        else key - value
 *        3) set of absent keys
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class HashCodeCollectorFunction extends FunctionAdapter {

    private static Logger logger = LoggerFactory.getLogger(HashCodeCollectorFunction.class);
    private static final long serialVersionUID = -7410041026699443297L;

    @SuppressWarnings("unchecked")
    @Override
    public void execute(FunctionContext functionContext) {

        Map<String, Object> result = new HashMap<String, Object>();
        Map<Object, Integer> hashCodeMap = new HashMap<Object, Integer>();
        Set<Object> absentKeys = new HashSet<Object>();

        result.put("map", hashCodeMap);
        result.put("absentKeys", absentKeys);

        logger.debug("start executing function..");
        System.out.println("start executing function..");

        String memberId = CacheFactory.getAnyInstance().getDistributedSystem().getDistributedMember().getId();
        //functionContext.getResultSender().sendResult(memberId);
        result.put("memberId", memberId);

        //if region doesn't exist we send empty result
        String regionName = (String) ((HashMap) functionContext.getArguments()).get("regionName");
        Region checkingRegion = CacheFactory.getAnyInstance().getRegion(regionName);
        if (checkingRegion == null) {
            /*functionContext.getResultSender().sendResult((HashSet) absentKeys);
            functionContext.getResultSender().lastResult((HashMap) hashCodeMap);*/
            functionContext.getResultSender().lastResult((HashMap) result);
            return;
        }

        Set keySet = (HashSet) ((HashMap)functionContext.getArguments()).get("keys");

        if (PartitionRegionHelper.isPartitionedRegion(checkingRegion)) {
            logger.debug("region is partitioned");
            System.out.println("region is partitioned");
            Region localRegion = PartitionRegionHelper.getLocalPrimaryData(checkingRegion);
            for (Object key : keySet) {
                if (localRegion.containsKey(key))
                    hashCodeMap.put(key, localRegion.get(key).hashCode());
                else
                    absentKeys.add(key);           //todo: locally it can be absent
            }
        } else {
            logger.debug("region isn't partitioned");
            System.out.println("region isn't partitioned");
            for (Object key : keySet)
                if (checkingRegion.containsKey(key))
                    hashCodeMap.put(key, checkingRegion.get(key).hashCode());
                else
                    absentKeys.add(key);
        }

        logger.trace("local map is: " + hashCodeMap);
        System.out.println("local map is " + hashCodeMap);

        /*functionContext.getResultSender().sendResult((HashSet) absentKeys);
        functionContext.getResultSender().lastResult((HashMap) hashCodeMap);*/
        functionContext.getResultSender().lastResult((HashMap) result);
        logger.debug("stop executing function..");
        System.out.println("stop executing");
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
