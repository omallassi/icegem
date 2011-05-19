package com.googlecode.icegem.message.barrier.core.plugins;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.googlecode.icegem.message.barrier.model.InnerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * User: akondratyev
 */
public class BarrierMessageRegionListener extends CacheListenerAdapter<Object, InnerMessage> implements Declarable {

    private static Logger logger = LoggerFactory.getLogger(BarrierMessageRegionListener.class);

    @Override
    public void afterCreate(EntryEvent<Object, InnerMessage> entryEvent) {
        logger.debug("create {}", entryEvent.getNewValue());
  /*      InnerMessage msg = entryEvent.getNewValue();
        Region<Object, List> msgSequenceRgn = entryEvent.getRegion().getRegionService().getRegion("msgs-sequence");
        List<Object> msgSequence;
        Lock lock = msgSequenceRgn.getDistributedLock(msg.getEntityId());
        try {
            lock.lock();
            if (msgSequenceRgn.containsKey(msg.getEntityId()))
                msgSequence = msgSequenceRgn.get(msg.getEntityId());
            else
                msgSequence = new ArrayList<Object>();

            msgSequence.add(msg.getId());

            msgSequenceRgn.put(msg.getEntityId(), msgSequence);
            logger.debug("msg {} add to sequence on entityId {}", new Object[]{msg.getId(), msg.getEntityId()});
        } finally {
            lock.unlock();
        }*/
    }

    @Override
    public void afterDestroy(EntryEvent<Object, InnerMessage> entryEvent) {
    }

    public void init(Properties properties) {
    }
}
