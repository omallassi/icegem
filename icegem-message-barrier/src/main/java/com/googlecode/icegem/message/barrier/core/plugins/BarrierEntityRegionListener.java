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
import java.util.concurrent.locks.Lock;


/**
 * User: akondratyev
 */
public class BarrierEntityRegionListener extends CacheListenerAdapter implements Declarable{

    private static Logger logger = LoggerFactory.getLogger(BarrierEntityRegionListener.class);
    private Region<Object, List<Object>> msgSequenceRegion;
    private Region msgToCheckRgn;

    @Override
    public void afterCreate(EntryEvent entryEvent) {
       logger.debug("create {}", entryEvent.getNewValue());
    }

    //todo:unnecessary do it on server side
    @Override
    public void afterUpdate(EntryEvent entryEvent) {
        Object entity = entryEvent.getNewValue();
        Object entityId = entryEvent.getKey();
        //logger.debug("update {}", entity);

        msgSequenceRegion = entryEvent.getRegion().getRegionService().getRegion("msgs-sequence");
        msgToCheckRgn = entryEvent.getRegion().getRegionService().getRegion("msgToCheck");

        if (!msgSequenceRegion.containsKey(entityId)) {        //msgs from other adapter
            logger.debug("msgSequenceRgn is empty");
            return;
        }


        List msgsSnapshot = msgSequenceRegion.get(entityId);
        Object[] msgs = msgsSnapshot.toArray();

        //logger.debug("msgs to check {}", msgs);

        for (Object msgId : msgs) {
            Lock nextIdLock = msgToCheckRgn.getDistributedLock(RegionListeningBarrierBean.NEXT_ID);
            try {
                nextIdLock.lock();
                if (msgToCheckRgn.containsValueForKey(RegionListeningBarrierBean.NEXT_ID))
                    msgToCheckRgn.put(msgToCheckRgn.get(RegionListeningBarrierBean.NEXT_ID), msgId);
                else {
                    msgToCheckRgn.put(RegionListeningBarrierBean.NEXT_ID, 0L);
                    msgToCheckRgn.put(0L, msgId);
                }
                long nextId = (Long) msgToCheckRgn.get(RegionListeningBarrierBean.NEXT_ID);
                nextId++;
                msgToCheckRgn.put(RegionListeningBarrierBean.NEXT_ID, nextId);
            }finally {
                nextIdLock.unlock();
            }

        }
    }

    public void init(Properties properties) {
    }
}
