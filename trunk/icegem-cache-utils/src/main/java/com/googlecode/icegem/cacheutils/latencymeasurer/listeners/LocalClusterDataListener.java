package com.googlecode.icegem.cacheutils.latencymeasurer.listeners;

import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.googlecode.icegem.cacheutils.latencymeasurer.utils.LatencyValueSequence;
import com.googlecode.icegem.cacheutils.latencymeasurer.LatencyMeasurerManager;
import com.googlecode.icegem.cacheutils.latencymeasurer.TestDataSender;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LocalClusterDataListener extends CacheListenerAdapter implements Declarable {
    private static final Logger log = LoggerFactory.getLogger(LatencyMeasurerManager.class);
    private Map<String, LatencyValueSequence> clusterLatencySecuencies = new HashMap<String, LatencyValueSequence>();

    public void afterUpdate(EntryEvent event) {
        String memberId = event.getDistributedMember().getId();
        String remoteClusterName = (String) event.getRegion().getParentRegion().get(memberId);
        if (!clusterLatencySecuencies.containsKey(remoteClusterName))
              clusterLatencySecuencies.put(remoteClusterName, new LatencyValueSequence(TestDataSender.getDataSeriesLength(), remoteClusterName));
        clusterLatencySecuencies.get(remoteClusterName).addItem((Long ) event.getNewValue(), (String) event.getKey());
        //log.info(event.getKey().toString() + ":" + event.getNewValue().toString()  + " was : " + event.getOldValue().toString() + " UPDATED form " + event.getDistributedMember().getId());
	}
    
	public void init(Properties p) {
	}
}
