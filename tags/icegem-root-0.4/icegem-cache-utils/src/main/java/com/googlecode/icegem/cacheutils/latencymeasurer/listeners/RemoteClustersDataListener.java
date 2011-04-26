package com.googlecode.icegem.cacheutils.latencymeasurer.listeners;

import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RemoteClustersDataListener extends CacheListenerAdapter implements Declarable {
    private static final Logger log = LoggerFactory.getLogger(RemoteClustersDataListener.class);

    public void init(Properties properties) {
    }

    public void afterCreate(EntryEvent event) {
        long receiveTime = System.nanoTime();
        Long startTime = (Long) event.getNewValue();
        event.getRegion().put(event.getKey(), receiveTime - startTime);
        //log.info(event.getKey().toString() + ":" + event.getNewValue().toString() + " CREATED form " + event.getDistributedMember().getId());
	}
}
