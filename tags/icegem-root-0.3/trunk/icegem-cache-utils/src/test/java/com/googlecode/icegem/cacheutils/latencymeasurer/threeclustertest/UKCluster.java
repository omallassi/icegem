package com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest;

/**** List of All Imported Classes ***/

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.gemstone.gemfire.cache.util.Gateway;
import com.gemstone.gemfire.cache.util.GatewayHub;
import com.gemstone.gemfire.cache.util.GatewayQueueAttributes;

import java.lang.Exception;

// End of Import

public  class  UKCluster

{


//  CLASS: com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest.UKCluster:
 public     UKCluster( )
 {
    super();
    return;

 }

//  CLASS: com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest.UKCluster:
 public  static    void main(  String [] args)
 throws Exception
 {
    Cache cache= null;
    CacheServer cacheServer= null;
    DiskStore diskStore= null;
    Gateway gatewayJP= null;
    Gateway gatewayUS= null;
    GatewayHub gatewayHub= null;
    GatewayQueueAttributes queueAttributes= null;
    Region region= null;

    //DynamicRegionFactory dynRegFactory = get();
    //dynRegFactory.registerDynamicRegionListener(new LatencyDynamicRegionListener("uk"));
    //dynRegFactory.open();

    CacheFactory JdecGenerated2 = new CacheFactory();
    cache=JdecGenerated2.set("mcast-port","0").set("license-type", "development").set("license-file", "/home/yaidarova/Downloads/gemfireLicense.zip").set("license-file", "/home/yaidarova/Downloads/gemfireLicense.zip").set("start-locator","localhost[18081]").create();
    cacheServer=cache.addCacheServer();
    cacheServer.setPort(40403);
    cacheServer.start();
    gatewayHub=cache.addGatewayHub("UK",33333);
    gatewayUS=gatewayHub.addGateway("US");
    gatewayUS.addEndpoint("US","localhost",22222);
    gatewayJP=gatewayHub.addGateway("JP");
    gatewayJP.addEndpoint("JP","localhost",44444);
    GatewayQueueAttributes JdecGenerated106 = new GatewayQueueAttributes();
    queueAttributes=JdecGenerated106;
    diskStore=cache.createDiskStoreFactory().create("ukDiskStore");
    queueAttributes.setDiskStoreName("ukDiskStore");
    gatewayUS.setQueueAttributes(queueAttributes);
    gatewayJP.setQueueAttributes(queueAttributes);
    gatewayHub.start();
    region=cache.createRegionFactory(RegionShortcut.REPLICATE).setEnableGateway(true).create("data");
    return;

 }


}