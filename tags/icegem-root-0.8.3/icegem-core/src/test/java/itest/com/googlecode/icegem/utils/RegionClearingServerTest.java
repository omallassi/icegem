package itest.com.googlecode.icegem.utils;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.RegionUtils;
import com.googlecode.icegem.utils.ServerTemplate;
import org.fest.assertions.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

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
    public void setUp() throws IOException, InterruptedException {
        startCacheServers();
        startServer();
    }

    @AfterClass
    public void tearDown() throws IOException, InterruptedException {
        cache.close();
        stopCacheServers();
    }

    @Test
    public void testRegionClearingForReplicatedRegion() {
        replicatedRegion1.create(1, 2);
        Assertions.assertThat(replicatedRegion1.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(replicatedRegion1.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(replicatedRegion1);
        Assertions.assertThat(replicatedRegion1.get(1)).as("Region entry has not been deleted").isNull();
        Assertions.assertThat(replicatedRegion1.keySet().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    @Test
    public void testRegionClearingForReplicatedRegionWithEmptyDataPolicy() {
        replicatedRegion2.create(1, 2);
        Assertions.assertThat(replicatedRegion2.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(replicatedRegion2.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(replicatedRegion2);
        Assertions.assertThat(replicatedRegion2.get(1)).as("Region entry has not been deleted").isNull();
        Assertions.assertThat(replicatedRegion2.keySet().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    @Test
    public void testRegionClearingForPartitionedRegion() {
        partitionedRegion1.create(1, 2);
        Assertions.assertThat(partitionedRegion1.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(partitionedRegion1.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(partitionedRegion1);
        Assertions.assertThat(partitionedRegion1.get(1)).as("Region entry has not been deleted").isNull();
        Assertions.assertThat(partitionedRegion1.keySet().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    @Test
    public void testRegionClearingForPartitionedRegionWithEmptyDataPolicy() {
        partitionedRegion2.create(1, 2);
        Assertions.assertThat(partitionedRegion2.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(partitionedRegion2.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(partitionedRegion2);
        Assertions.assertThat(partitionedRegion2.get(1)).as("Region entry has not been deleted").isNull();
        Assertions.assertThat(partitionedRegion2.keySet().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    /**
     * Starts a cache server.
     * @throws java.io.IOException
     */
    private void startServer() throws IOException {
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
    private void startCacheServers() throws IOException, InterruptedException {
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
    private void stopCacheServers() throws IOException, InterruptedException {
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
    }
}
