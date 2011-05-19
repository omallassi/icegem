package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.googlecode.icegem.message.barrier.model.InnerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * User: akondratyev
 */
public class ExpiredMessageCollectorFunction implements Function, Declarable {

    private static Logger logger = LoggerFactory.getLogger(ExpiredMessageCollectorFunction.class);
    private static int COLLECTING_MSG_COUNT = 1000;

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public void execute(FunctionContext functionContext) {
        logger.trace("start gathering expired messages");
        RegionFunctionContext context = (RegionFunctionContext) functionContext;
        Region currentRegion = context.getDataSet();
        Region messageRegion = currentRegion.getRegionService().getRegion("msgs");
        Region lockingRegion = currentRegion.getRegionService().getRegion("lock-expired-msgs");     //todo: replace with DistributedLockService

        if (currentRegion.isEmpty()) {
            context.getResultSender().lastResult(new HashMap());
            return;
        }

        Map eventSequences = new HashMap(COLLECTING_MSG_COUNT);
        Lock regionLock = lockingRegion.getRegionDistributedLock();
        try {
            regionLock.lock();
            for (Object key : currentRegion.keySet()) {
                if (!currentRegion.containsValueForKey(key))
                    continue;

                InnerMessage innerMessage = (InnerMessage) currentRegion.get(key);
                Object entityId = innerMessage.getEntityId();
                List messageSequence;
                if (eventSequences.containsKey(entityId))
                    messageSequence = (List) eventSequences.get(entityId);
                else
                    messageSequence = new ArrayList();
                messageSequence.add(innerMessage.getId());

                eventSequences.put(entityId, messageSequence);
                //return msg to normal region
                messageRegion.put(innerMessage.getId(), innerMessage);
                currentRegion.remove(key);
                if (eventSequences.size() >= COLLECTING_MSG_COUNT)
                    break;
            }
        } finally {
            regionLock.unlock();
        }
        //return msg sequence to barrier bean
        context.getResultSender().lastResult((HashMap) eventSequences);
    }

    @Override
    public String getId() { return getClass().getName(); }

    @Override
    public boolean optimizeForWrite() { return true; }

    @Override
    public boolean isHA() { return true; }

    @Override
    public void init(Properties properties) {}
}
