package com.googlecode.icegem.serialization.example.web;


import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.RegionEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;

import java.util.Properties;

/**
 * @author igolovach
 */
public class ClientCacheListener<K, V> extends CacheListenerAdapter<K, V> implements Declarable {

    public void afterCreate(EntryEvent<K, V> e) {
        System.out.println("\n----------------------------");
        System.out.println("GETTING ENTRY: \nKEY:" + e.getKey() + "\nVALUE:\n" + e.getNewValue());
    }

    public void afterUpdate(EntryEvent<K, V> e) {
        System.out.println("\n----------------------------");
        System.out.println("GETTING ENTRY: \nKEY:" + e.getKey() + "\nVALUE:\n" + e.getNewValue());
    }

    public void afterDestroy(EntryEvent<K, V> e) {
        System.out.println("    Received afterDestroy event for entry: " +
                e.getKey());
    }

    public void afterInvalidate(EntryEvent<K, V> e) {
        System.out.println("    Received afterInvalidate event for entry: " +
                e.getKey());
    }

    public void afterRegionLive(RegionEvent e) {
        System.out.println("    Received afterRegionLive event, sent to durable clients after \nthe server has finished replaying stored events.  ");
    }

    public void init(Properties props) {
        // do nothing
    }
}


