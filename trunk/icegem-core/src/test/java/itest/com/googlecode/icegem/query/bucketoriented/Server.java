package itest.com.googlecode.icegem.query.bucketoriented;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.googlecode.icegem.utils.CacheUtils;
import com.googlecode.icegem.utils.ConsoleUtils;
import com.googlecode.icegem.utils.JavaProcessLauncher;

import java.io.IOException;

/**
 * A simple cache server that stores partition region data.
 * Use JavaProcessLauncher to launch this cache server from tests.
 * @see JavaProcessLauncher
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
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        startCacheServer(CacheUtils.startLocator(args));

        System.out.println("Cache Server has been started");

        ConsoleUtils.waitForEnter(JavaProcessLauncher.PROCESS_STARTUP_COMPLETED);

        stopCacheServer();

        System.err.println("Cache Server has been stopped");
    }

    /**
     * Creates cache based on API configuration.
     *
     * @param startLocator flag that indicates that embedded locator should be started
     * @throws java.io.IOException
     */
    public static void startCacheServer(boolean startLocator) throws IOException {
        AttributesFactory<Object, Object> attributesFactory = new AttributesFactory<Object, Object>();
        attributesFactory.setDataPolicy(DataPolicy.PARTITION);
        PartitionAttributes partitionAttributes = new PartitionAttributesFactory().setTotalNumBuckets(10).create();
        attributesFactory.setPartitionAttributes(partitionAttributes);
        RegionAttributes<Object, Object> regionAttributes = attributesFactory.create();

        CacheFactory cacheFactory = new CacheFactory()
                .set("mcast-port", "0")
                .set("log-level", "warning");
        if (startLocator) {
            cacheFactory.set("start-locator", "localhost[" + LOCATOR_PORT + "]").create();
        } else {
            cacheFactory.set("locators", "localhost[" + LOCATOR_PORT + "]");
        }

        cache = cacheFactory.create();

        cache.createRegionFactory(regionAttributes).create("data");
        CacheServer cacheServer = cache.addCacheServer();

// Use a specific port for working from a client only with a one server.
// In this case the client should establish direct connection to this server via it's pool.
//        cacheServer.setPort(10366);

        cacheServer.setPort(0);
        cacheServer.start();
    }
    
    /**
     * Stops cache server.
     */
    public static void stopCacheServer() {
        cache.close();
    }
}

