package com.googlecode.icegem.message.barrier.core.plugins;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * User: akondratyev
 */
public class ServerPeerMessageRegionListener extends CacheListenerAdapter implements Declarable {

    private static final Logger logger = LoggerFactory.getLogger(ServerPeerMessageRegionListener.class);
    private int totalDestroyed = 0;                             //todo: remove

    @Override
    public void afterCreate(EntryEvent entryEvent) {
        //logger.debug("create {}", entryEvent.getNewValue());
    }

    @Override
    public void afterInvalidate(EntryEvent entryEvent) {
        Region expiredMsg = entryEvent.getRegion().getRegionService().getRegion("expired-msgs");
        Object msg = entryEvent.getOldValue();
        Object msgId = entryEvent.getKey();
        logger.debug("msg {} expired and stored in saving region", msgId);
        expiredMsg.put(msgId, msg);
    }

    @Override
    public void afterUpdate(EntryEvent entryEvent) {
        logger.debug("update {}", entryEvent.getNewValue());
    }

    @Override
    public void afterDestroy(EntryEvent entryEvent) {
        totalDestroyed++;
        if (totalDestroyed % 1000 == 0) {
            logger.info("now destroyed msg count: {}", totalDestroyed);
        }
        logger.debug("destroy {}", entryEvent.getKey());
    }

    public void init(Properties properties) {

    }
}
