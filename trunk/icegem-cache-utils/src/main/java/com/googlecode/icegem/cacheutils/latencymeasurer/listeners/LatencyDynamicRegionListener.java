package com.googlecode.icegem.cacheutils.latencymeasurer.listeners;

import com.gemstone.gemfire.cache.DynamicRegionListener;
import com.gemstone.gemfire.cache.RegionEvent;
import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.cacheutils.latencymeasurer.listeners.RemoteClustersDataListener;
import com.googlecode.icegem.cacheutils.latencymeasurer.listeners.LocalClusterDataListener;
import com.googlecode.icegem.cacheutils.latencymeasurer.TestDataSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LatencyDynamicRegionListener implements DynamicRegionListener {
    private static final Logger log = LoggerFactory.getLogger(LatencyDynamicRegionListener.class);
    private String localClusterName;
    private int measureFrequency;
    private TestDataSender dataSender;
    private Region localClusterLatencyRegion;
    private boolean remoteRegionExistes;

    public LatencyDynamicRegionListener(String localClusterName, int measureFrequency) {
        this.localClusterName = localClusterName;
        this.measureFrequency = measureFrequency;
    }


    public void beforeRegionCreate(String s, String s1) {

    }
    
    public void afterRegionCreate(RegionEvent<?, ?> regionEvent) {
        Region region = regionEvent.getRegion();
        if (region.getFullPath().equals("/latency_data/" + localClusterName)) {
            region.getAttributesMutator().addCacheListener(new LocalClusterDataListener());
            localClusterLatencyRegion = region;
            if (dataSender == null && remoteRegionExistes) {
                startDataSender();
            } else if (dataSender == null) {
                log.info("Waiting for members in remote clusters...");
            }
            return;
        }
        log.info("Admitting tool for latency measure in remote cluster");
        if (!remoteRegionExistes)
            remoteRegionExistes = true;
        region.getAttributesMutator().addCacheListener(new RemoteClustersDataListener());
        if (dataSender == null && localClusterLatencyRegion != null) {
            startDataSender();
        }
    }

    private void startDataSender() {
        log.info("Starting test data sender for measuring latency");
        dataSender = new TestDataSender(localClusterLatencyRegion, measureFrequency);
        dataSender.start();
    }

    public void beforeRegionDestroy(RegionEvent<?, ?> regionEvent) {
        Region region = regionEvent.getRegion();
        if (region.getFullPath().equals("/latency_data/" + localClusterName)) {
            log.info("Stopping test data sender for measuring latency");
            localClusterLatencyRegion = null;
            dataSender.stop();
            dataSender = null;
        } else if (region.getParentRegion().subregions(false).size() == 2) {
            log.info("Stopping test data sender for measuring latency");
            remoteRegionExistes = false;
            dataSender.stop();
            dataSender = null;
        }
    }

    public void afterRegionDestroy(RegionEvent<?, ?> regionEvent) {
        Region region = regionEvent.getRegion();
        if (region.getFullPath().equals("/latency_data/" + localClusterName)) {
            log.info("Local latency region was destroyed");
        } else {
            log.info("Remote latency region was destroyed in member " + regionEvent.getDistributedMember().getId());
        }
    }
}
