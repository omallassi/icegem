package com.googlecode.icegem.core.functions.bucketoriented.server;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.googlecode.icegem.core.functions.bucketoriented.common.utils.ConsoleUtils;

/**
 * Simple server that stores partition region data.
 *
 * @author Andrey Stepanov aka standy
 */
public class Server {
    /** Field cache  */
    private static Cache cache;

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
     */
    public static void createCacheAndRegion() {
        cache = new CacheFactory()
                .set("cache-xml-file", "server-cache.xml")
                .create();
        System.out.println("Cache Server has been started");
    }
}

