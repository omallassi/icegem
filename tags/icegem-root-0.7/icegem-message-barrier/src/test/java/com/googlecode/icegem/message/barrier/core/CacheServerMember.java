package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.*;
import com.googlecode.icegem.utils.ConsoleUtils;
import com.googlecode.icegem.utils.JavaProcessLauncher;

import java.io.IOException;

/**
 * User: akondratyev
 */
public class CacheServerMember {
/*    private Cache cache;
    private String cacheXmlFile;
    public CacheServerMember() {
    }

    public void start() {
        cache = new CacheFactory().set("cache-xml-file", cacheXmlFile).create();
    }

    public void setCacheXmlFile(String cacheXmlFile) {
        this.cacheXmlFile = cacheXmlFile;
    }

    public void stop() {
        if (cache != null)
            cache.close();
    }*/


    //todo: from icegem-core
    private static Cache cache;
    private static final int LOCATOR_PORT = 10355;

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length ==0)
            throw new NullPointerException("need cache-xml-arg");
        createCacheAndRegion(args);

        System.out.println("Cache Server has been started");

        ConsoleUtils.waitForEnter(JavaProcessLauncher.PROCESS_STARTUP_COMPLETED);

        System.err.println("Cache Server has been stopped");

        cache.close();
    }

    public static void createCacheAndRegion(String[] args) throws IOException {
        CacheFactory cacheFactory = new CacheFactory();
        System.out.println("cache-xml-file: " + args[0]);
        cache = cacheFactory
                .set("mcast-port", "0")
                .set("log-level", "warning")
                .set("locators", "localhost[" + LOCATOR_PORT + "]")
                .set("cache-xml-file", args[0])
                .create();
    }
}
