package com.googlecode.icegem.utils;

import com.gemstone.gemfire.cache.*;

/**
 * Server template for using in tests.
 *
 * Use JavaProcessLauncher.runServerWithConfirmation(Class klass, String pathToServerPropertiesFile)
 * to launch this cache server from tests. All peer/server configurations should be passed via properties file.
 *
 * @see JavaProcessLauncher
 *
 * @author Andrey Stepanov aka standy
 */
public class ServerTemplate {
    /** Field cache  */
    private static Cache cache;

    /**
     * Server entry point.
     *
     * @param args of type String[]
     */
    public static void main(String[] args) {
        startCacheServer();
        
        System.out.println("Cache Server has been started");

        ConsoleUtils.waitForEnter(JavaProcessLauncher.PROCESS_STARTUP_COMPLETED);
        
        stopCacheServer();

        System.err.println("Cache Server has been stopped");
    }

    public static void startCacheServer() {
        cache = new CacheFactory().create();
    }

    /**
     * Stops cache server.
     */
    public static void stopCacheServer() {
        cache.close();
    }
}


