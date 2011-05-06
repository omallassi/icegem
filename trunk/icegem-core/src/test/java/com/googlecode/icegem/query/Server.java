package com.googlecode.icegem.query;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.server.CacheServer;

import java.io.IOException;

/**
 * TODO: Starts this server automatically before tests.
 *
 * Simple server that stores partition region data.
 *
 * @author Andrey Stepanov aka standy
 */
public class Server {
    /** Field cache  */
    private static Cache cache;
    /** Field LOCATOR_PORT  */
    private static final int LOCATOR_PORT = 10355;

    /**
     * Server entry point.
     *
     * @param args of type String[]
     * @throws Exception when
     */
    public static void main(String[] args) throws Exception {
        createCacheAndRegion();

        ConsoleUtils.waitForEnter("Press 'Enter' to stop server");

        System.out.println("Cache Server has been stopped");
        cache.close();
    }

    /**
     * Creates cache based on cache xml.
     * @throws java.io.IOException
     */
    public static void createCacheAndRegion() throws IOException {
        AttributesFactory attributesFactory = new AttributesFactory();
        attributesFactory.setDataPolicy(DataPolicy.PARTITION);
        PartitionAttributes partitionAttributes = new PartitionAttributesFactory().setTotalNumBuckets(10).create();
        attributesFactory.setPartitionAttributes(partitionAttributes);
        RegionAttributes regionAttributes = attributesFactory.create();

        CacheFactory cacheFactory = new CacheFactory();
        Cache cache = cacheFactory.set("mcast-port", "0").set("log-level", "warning").set("locators", "localhost[" + LOCATOR_PORT + "]").create();
        Region<Object, Object> data = cache.createRegionFactory(regionAttributes).create("data");
        CacheServer cacheServer = cache.addCacheServer();
        cacheServer.setPort(0);
        cacheServer.start();
        System.out.println("Cache Server has been started");
    }
}

