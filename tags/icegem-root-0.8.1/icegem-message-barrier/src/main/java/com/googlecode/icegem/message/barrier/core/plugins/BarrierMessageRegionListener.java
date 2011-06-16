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
        logger.trace("create {}", entryEvent.getNewValue());
    }

    @Override
    public void afterDestroy(EntryEvent<Object, InnerMessage> entryEvent) {
    }

    public void init(Properties properties) {
    }
}
