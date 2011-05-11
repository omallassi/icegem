package itest.com.googlecode.icegem.query.bucketoriented;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.server.CacheServer;
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
        createCacheAndRegion();

        System.out.println("Cache Server has been started");

        ConsoleUtils.waitForEnter(JavaProcessLauncher.PROCESS_STARTUP_COMPLETED);

        System.err.println("Cache Server has been stopped");
        
        cache.close();
    }

    /**
     * Creates cache based on cache xml.
     * @throws java.io.IOException
     */
    public static void createCacheAndRegion() throws IOException {
        AttributesFactory<Object, Object> attributesFactory = new AttributesFactory<Object, Object>();
        attributesFactory.setDataPolicy(DataPolicy.PARTITION);
        PartitionAttributes partitionAttributes = new PartitionAttributesFactory().setTotalNumBuckets(10).create();
        attributesFactory.setPartitionAttributes(partitionAttributes);
        RegionAttributes<Object, Object> regionAttributes = attributesFactory.create();

        CacheFactory cacheFactory = new CacheFactory();

        cache = cacheFactory
                .set("mcast-port", "0")
                .set("log-level", "warning")
                .set("locators", "localhost[" + LOCATOR_PORT + "]")
                .create();

        cache.createRegionFactory(regionAttributes).create("data");
        CacheServer cacheServer = cache.addCacheServer();
        cacheServer.setPort(0);
        cacheServer.start();
    }
}

