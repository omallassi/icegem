package com.googlecode.icegem.message.barrier.core.plugins;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: akondratyev
 */
public class BarrierExpiredMessageRegionListener extends CacheListenerAdapter implements Declarable{

    private static Logger logger = LoggerFactory.getLogger(BarrierExpiredMessageRegionListener.class);
    private AtomicInteger expiredMsgCount = new AtomicInteger(0);

    @Override
    public void afterCreate(EntryEvent entryEvent) {
        logger.trace("create {} ", entryEvent.getNewValue());
        expiredMsgCount.incrementAndGet();
    }

    @Override
    public void afterDestroy(EntryEvent entryEvent) {
        logger.trace("destroy {} ", entryEvent.getOldValue());
    }

    @Override
    public void afterInvalidate(EntryEvent entryEvent) {
        logger.trace("invalidate {} ", entryEvent.getOldValue());
    }

    public int getExpiredMsgCount() {
        return expiredMsgCount.get();
    }

    
    public void init(Properties properties) {}
}
