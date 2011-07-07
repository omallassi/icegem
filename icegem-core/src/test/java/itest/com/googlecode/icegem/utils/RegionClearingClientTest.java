package itest.com.googlecode.icegem.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.CacheUtils;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 * Tests for checking region clearing from client side.
 *
 * @author Andrey Stepanov aka standy
 */
public class RegionClearingClientTest {
    /** Field cache  */
    private static ClientCache cache;
    /** Field replicatedRegion1  */
    private static Region<Object, Object> replicatedRegion1;
    /** Field replicatedRegion2  */
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
    public static void setUp() throws IOException, InterruptedException, TimeoutException {
        startCacheServers();
        startClient();
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
        cache.close();
        stopCacheServers();
    }

    @Test
    public void testRegionClearingForReplicatedRegionViaClientProxy() {
        replicatedRegion1.create(1, 2);
        assertNotNull(replicatedRegion1.get(1));
        assertEquals(replicatedRegion1.get(1), 2);
        CacheUtils.clearRegion(replicatedRegion1);
        assertNull(replicatedRegion1.get(1));
        assertEquals(replicatedRegion1.keySetOnServer().size(), 0);
    }

    @Test
    public void testRegionClearingForReplicatedRegionViaClientCachingProxy() {
        replicatedRegion2.create(1, 2);
        assertNotNull(replicatedRegion2.get(1));
        assertEquals(replicatedRegion2.get(1), 2);
        CacheUtils.clearRegion(replicatedRegion2);
        replicatedRegion2.localClear();
        assertNull(partitionedRegion2.get(1));
        assertEquals(replicatedRegion1.keySetOnServer().size(), 0);
    }

    @Test
    public void testRegionClearingForPartitionedRegionViaClientProxy() {
        partitionedRegion1.create(1, 2);
        assertNotNull(partitionedRegion1.get(1));
        assertEquals(partitionedRegion1.get(1), 2);
        CacheUtils.clearRegion(partitionedRegion1);
        assertNull(partitionedRegion1.get(1));
        assertEquals(partitionedRegion1.keySetOnServer().size(), 0);
    }

    @Test
    public void testRegionClearingForPartitionedRegionViaClientCachingProxy() {
        partitionedRegion2.create(1, 2);
        assertNotNull(partitionedRegion2.get(1));
        assertEquals(partitionedRegion2.get(1), 2);
        CacheUtils.clearRegion(partitionedRegion2);
        partitionedRegion2.localClear();
        assertNull(partitionedRegion2.get(1));
        assertEquals(partitionedRegion2.keySetOnServer().size(), 0);
    }

    /**
     * Starts a client.
     * @throws java.io.IOException
     */
    private static void startClient() throws IOException {
        PropertiesHelper properties = new PropertiesHelper("/regionClearingProperties.properties");

        cache = new ClientCacheFactory()
                .set("cache-xml-file", "regionClearingTestClientCache.xml")
                .set("log-level", properties.getStringProperty("log-level"))
                .set("license-file", properties.getStringProperty("license-file"))
                .set("license-type", properties.getStringProperty("license-type"))
                .create();

        replicatedRegion1 = cache.getRegion("replicated_region_1");
        partitionedRegion1 = cache.getRegion("partitioned_region_1");

        replicatedRegion2 = cache.getRegion("replicated_region_2");
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
                null);
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
