package com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest;

/**** List of All Imported Classes ***/

import com.gemstone.gemfire.cache.*;
import static com.gemstone.gemfire.cache.DynamicRegionFactory.get;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.gemstone.gemfire.cache.util.Gateway;
import com.gemstone.gemfire.cache.util.GatewayHub;
import com.gemstone.gemfire.cache.util.GatewayQueueAttributes;
import com.googlecode.icegem.cacheutils.latencymeasurer.listeners.LatencyDynamicRegionListener;

import java.lang.Exception;

// End of Import

public  class  JPCluster

{


//  CLASS: com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest.JPCluster:
 public     JPCluster( ) 
 {
    super();
    return;

 }

//  CLASS: com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest.JPCluster:
 public  static    void main(  String [] args)
 throws Exception
 {
    Cache cache= null;
    CacheServer cacheServer= null;
    DiskStore diskStore= null;
    Gateway ukGateway= null;
    Gateway usGateway= null;
    GatewayHub gatewayHub= null;
    GatewayQueueAttributes queueAttributes= null;
    Region region= null;


    
    CacheFactory JdecGenerated2 = new CacheFactory();
    cache=JdecGenerated2.set("mcast-port","0").set("license-type", "development").set("license-file", "/home/yaidarova/Downloads/gemfireLicense.zip").set("start-locator","localhost[18083]").create();
    cacheServer=cache.addCacheServer();
    cacheServer.setPort(40406);
    cacheServer.start();
    gatewayHub=cache.addGatewayHub("JP",44444);
    ukGateway=gatewayHub.addGateway("UK");
    ukGateway.addEndpoint("UK","localhost",33333);
    usGateway=gatewayHub.addGateway("US");
    usGateway.addEndpoint("US","localhost",22222);
    GatewayQueueAttributes JdecGenerated106 = new GatewayQueueAttributes();
    queueAttributes=JdecGenerated106;
    diskStore=cache.createDiskStoreFactory().create("jpDiskStore");
    queueAttributes.setDiskStoreName("jpDiskStore");
    ukGateway.setQueueAttributes(queueAttributes);
    usGateway.setQueueAttributes(queueAttributes);
    gatewayHub.start();
    region=cache.createRegionFactory(RegionShortcut.REPLICATE).setEnableGateway(true).create("data");
    return;

 }


}