package com.googlecode.icegem.message.barrier.core.plugins;

import com.googlecode.icegem.message.barrier.core.RegionListeningBarrierBean;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;


/**
 * User: akondratyev
 */
public class BarrierEntityRegionListener extends CacheListenerAdapter implements Declarable {

    private static Logger logger = LoggerFactory.getLogger(BarrierEntityRegionListener.class);
    private Region<Object, List<Object>> messageSequenceRegion;
    private Region messageToCheckRegion;
    private String messageSequenceRegionName;
    private String messageToCheckRegionName;
    private Executor exec = Executors.newSingleThreadExecutor();

    @Override
    public void afterCreate(EntryEvent entryEvent) {
        logger.trace("create {}", entryEvent.getNewValue());
    }

    @Override
    public void afterUpdate(EntryEvent entryEvent) {
        Object entityId = entryEvent.getKey();
        //logger.debug("update {}", entity);
        messageSequenceRegion = entryEvent.getRegion().getRegionService().getRegion(messageSequenceRegionName);
        messageToCheckRegion = entryEvent.getRegion().getRegionService().getRegion(messageToCheckRegionName);

        if (!messageSequenceRegion.containsKey(entityId)) {        //msgs from another adapter
            logger.debug("msgSequenceRgn is empty");
            return;
        }

        exec.execute(new CheckMessageTask(messageSequenceRegion, messageToCheckRegion, entityId));

    }

    public static class CheckMessageTask implements Runnable {
        private Region messageSequenceRegion;
        private Region messageToCheckRegion;
        private Object key;

        public CheckMessageTask(Region messageSequenceRegion, Region messageToCheckRegion, Object key) {
            this.messageSequenceRegion = messageSequenceRegion;
            this.messageToCheckRegion = messageToCheckRegion;
            this.key = key;
        }

        public void run() {
            List msgsSnapshot = (List) messageSequenceRegion.get(key);
            Object[] msgs = msgsSnapshot.toArray();

            //logger.debug("msgs to check {}", msgs);

            for (Object msgId : msgs) {
                Lock nextIdLock = messageToCheckRegion.getDistributedLock(RegionListeningBarrierBean.NEXT_ID);
                try {
                    nextIdLock.lock();
                    if (messageToCheckRegion.containsValueForKey(RegionListeningBarrierBean.NEXT_ID))
                        messageToCheckRegion.put(messageToCheckRegion.get(RegionListeningBarrierBean.NEXT_ID), msgId);
                    else {
                        messageToCheckRegion.put(RegionListeningBarrierBean.NEXT_ID, 0L);
                        messageToCheckRegion.put(0L, msgId);
                    }
                    long nextId = (Long) messageToCheckRegion.get(RegionListeningBarrierBean.NEXT_ID);
                    nextId++;
                    messageToCheckRegion.put(RegionListeningBarrierBean.NEXT_ID, nextId);
                } finally {
                    nextIdLock.unlock();
                }

            }
        }
    }

    public void init(Properties properties) {
        if (properties.getProperty("messageSequenceRegionName") == null ||
                properties.getProperty("messageToCheckRegionName") == null)
            throw new RuntimeException("set params \'messageSequenceRegionName\' and \'messageToCheckRegionName\' for " + getClass().getName());
        messageSequenceRegionName = properties.getProperty("messageSequenceRegionName");
        messageToCheckRegionName = properties.getProperty("messageToCheckRegionName");
    }
}
