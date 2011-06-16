package com.googlecode.icegem.message.barrier.core.plugins;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: akondratyev
 */
public class ServerPeerMessageRegionListener extends CacheListenerAdapter implements Declarable {

    private static final Logger logger = LoggerFactory.getLogger(ServerPeerMessageRegionListener.class);
    private Executor exec = Executors.newSingleThreadExecutor();
    private String expiredMessageRegionName;

    @Override
    public void afterCreate(EntryEvent entryEvent) {
        //logger.debug("create {}", entryEvent.getNewValue());
    }

    @Override
    public void afterInvalidate(EntryEvent entryEvent) {
        final Region expiredMsg = entryEvent.getRegion().getRegionService().getRegion(expiredMessageRegionName);
        final Object msg = entryEvent.getOldValue();
        final Object msgId = entryEvent.getKey();
        exec.execute(new Runnable() {
            public void run() {
                logger.trace("msg {} expired and stored in saving region", msgId);
                expiredMsg.put(msgId, msg);
            }
        });

    }

    @Override
    public void afterUpdate(EntryEvent entryEvent) {
        logger.trace("update {}", entryEvent.getNewValue());
    }

    @Override
    public void afterDestroy(EntryEvent entryEvent) {
        logger.trace("destroy {}", entryEvent.getKey());
    }

    public void init(Properties properties) {
        if (properties.getProperty("expiredMessageRegionName") == null)
            throw new RuntimeException("set property expiredMessageRegionName for " + this.getClass().getName());
        expiredMessageRegionName = properties.getProperty("expiredMessageRegionName");
    }
}
