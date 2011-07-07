package itest.com.googlecode.icegem.utils;

import java.io.IOException;


import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.RegionUtils;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 *
 * Tests for checking region clearing from server side.
 *
 * @author Andrey Stepanov aka standy
 */
public class RegionClearingServerTest {
    /** Field cache  */
    private static Cache cache;
    /** Field replicatedRegion1  */
    private static Region<Object, Object> replicatedRegion1;
    /** Field rep¯licatedRegion2  */
    private static Region<Object, Object> replicatedRegion2;
    /** Field partitionedRegion1  */
    private static Region<Object, Object> partitionedRegion1;
    /** Field partitionedRegion2  */
    private static Region<Object, Object> partitionedRegion2;
    /** Field cacheServer1  */
    private static Process cacheServer1;
    /** Field cacheServer2  */
    private static Process cacheServer2;
    /** Field javaProcessLauncher  */
    private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        startCacheServers();
        startServer();
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
        cache.close();
        stopCacheServers();
    }

    @Test
    public void testRegionClearingForReplicatedRegion() {
        replicatedRegion1.create(1, 2);
        assertNotNull(replicatedRegion1.get(1));
        assertEquals(replicatedRegion1.get(1), 2);
        RegionUtils.clearRegion(replicatedRegion1);
        assertNull(replicatedRegion1.get(1));
        assertEquals(replicatedRegion1.keySet().size(), 0);
    }

    @Test
    public void testRegionClearingForReplicatedRegionWithEmptyDataPolicy() {
        replicatedRegion2.create(1, 2);
        assertNotNull(replicatedRegion2.get(1));
        assertEquals(replicatedRegion2.get(1), 2);
        RegionUtils.clearRegion(replicatedRegion2);
        assertNull(replicatedRegion2.get(1));
        assertEquals(replicatedRegion2.keySet().size(), 0);
    }

    @Test
    public void testRegionClearingForPartitionedRegion() {
        partitionedRegion1.create(1, 2);
        assertNotNull(partitionedRegion1.get(1));
        assertEquals(partitionedRegion1.get(1), 2);
        RegionUtils.clearRegion(partitionedRegion1);
        assertNull(partitionedRegion1.get(1));
        assertEquals(partitionedRegion1.keySet().size(), 0);
    }

    @Test
    public void testRegionClearingForPartitionedRegionWithEmptyDataPolicy() {
        partitionedRegion2.create(1, 2);
        assertNotNull(partitionedRegion2.get(1));
        assertEquals(partitionedRegion2.get(1), 2);
        RegionUtils.clearRegion(partitionedRegion2);
        assertNull(partitionedRegion2.get(1));
        assertEquals(partitionedRegion2.keySet().size(), 0);
    }

    /**
     * Starts a cache server.
     * @throws java.io.IOException
     */
    private static void startServer() throws IOException {
        PropertiesHelper properties = new PropertiesHelper("/regionClearingProperties.properties");

        cache = new CacheFactory()
                .set("cache-xml-file", "regionClearingTestServerCache.xml")
                .set("log-level", properties.getStringProperty("log-level"))
                .set("mcast-port", properties.getStringProperty("mcast-port"))
                .set("locators", properties.getStringProperty("locators"))
                .set("license-file", properties.getStringProperty("license-file"))
                .set("license-type", properties.getStringProperty("license-type"))
                .create();
        replicatedRegion1 = cache.getRegion("replicated_region_1");
        replicatedRegion2 = cache.getRegion("replicated_region_2");
        partitionedRegion1 = cache.getRegion("partitioned_region_1");
        partitionedRegion2 = cache.getRegion("partitioned_region_2");
    }

    /**
     * Starts two cache servers for tests.
     *
     * @throws IOException when
     * @throws InterruptedException when
     */
    private static void startCacheServers() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class,
                new String[]{"-DgemfirePropertyFile=regionClearingProperties.properties"},
                null);
        cacheServer2 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class,
                new String[]{"-DgemfirePropertyFile=regionClearingProperties.properties"},
                null
        );
    }

    /**
     * Stops cache servers.
     *
     * @throws IOException when
     * @throws InterruptedException
     */
    private static void stopCacheServers() throws IOException, InterruptedException {
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
    }
}
