package com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest;

/**** List of All Imported Classes ***/

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.gemstone.gemfire.cache.util.Gateway;
import com.gemstone.gemfire.cache.util.GatewayHub;
import com.gemstone.gemfire.cache.util.GatewayQueueAttributes;

import java.lang.Exception;

// End of Import

public  class  USCluster

{


//  CLASS: com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest.USCluster:
 public     USCluster( )
 {
    super();
    return;

 }

//  CLASS: com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest.USCluster:
 public  static    void main(  String [] args)
 throws Exception
 {
    Cache cache= null;
    CacheServer cacheServer= null;
    DiskStore diskStore= null;
    Gateway gateway= null;
    Gateway gatewayJP= null;
    GatewayHub gatewayHub= null;
    GatewayQueueAttributes queueAttributes= null;
    Region region= null;

    CacheFactory JdecGenerated2 = new CacheFactory();
    cache=JdecGenerated2.set("mcast-port","0").set("license-type", "development").set("license-file", "/home/yaidarova/Downloads/gemfireLicense.zip").set("start-locator","localhost[18082]").create();
    cacheServer=cache.addCacheServer();
    cacheServer.setPort(40405);
    cacheServer.start();
    gatewayHub=cache.addGatewayHub("US",22222);
    gateway=gatewayHub.addGateway("UK");
    gateway.addEndpoint("UK","localhost",33333);
    gatewayJP=gatewayHub.addGateway("JP");
    gatewayJP.addEndpoint("JP","localhost",44444);
    GatewayQueueAttributes JdecGenerated106 = new GatewayQueueAttributes();
    queueAttributes=JdecGenerated106;
    diskStore=cache.createDiskStoreFactory().create("usDiskStore");
    queueAttributes.setDiskStoreName("usDiskStore");
    gateway.setQueueAttributes(queueAttributes);
    gatewayJP.setQueueAttributes(queueAttributes);
    gatewayHub.start();
    region=cache.createRegionFactory(RegionShortcut.REPLICATE).setEnableGateway(true).create("data");
    return;

 }


}