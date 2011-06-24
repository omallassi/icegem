package itest.com.googlecode.icegem.utils;

import com.gemstone.gemfire.cache.InterestResultPolicy;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.RegionUtils;
import com.googlecode.icegem.utils.ServerTemplate;
import org.fest.assertions.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

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
    public void setUp() throws IOException, InterruptedException, TimeoutException {
        startCacheServers();
        startClient();
    }

    @AfterClass
    public void tearDown() throws IOException, InterruptedException {
        cache.close();
        stopCacheServers();
    }

    @Test
    public void testRegionClearingForReplicatedRegionViaClientProxy() {
        replicatedRegion1.create(1, 2);
        Assertions.assertThat(replicatedRegion1.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(replicatedRegion1.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(replicatedRegion1);
        Assertions.assertThat(replicatedRegion1.get(1)).as("Region entry has not been deleted").isNull();
        Assertions.assertThat(replicatedRegion1.keySetOnServer().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    @Test
    public void testRegionClearingForReplicatedRegionViaClientCachingProxy() {
        replicatedRegion2.create(1, 2);
        Assertions.assertThat(replicatedRegion2.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(replicatedRegion2.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(replicatedRegion2);
        Assertions.assertThat(replicatedRegion1.keySetOnServer().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    @Test
    public void testRegionClearingForPartitionedRegionViaClientProxy() {
        partitionedRegion1.create(1, 2);
        Assertions.assertThat(partitionedRegion1.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(partitionedRegion1.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(partitionedRegion1);
        Assertions.assertThat(partitionedRegion1.get(1)).as("Region entry has not been deleted").isNull();
        Assertions.assertThat(partitionedRegion1.keySetOnServer().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    @Test
    public void testRegionClearingForPartitionedRegionViaClientCachingProxy() {
        partitionedRegion2.create(1, 2);
        Assertions.assertThat(partitionedRegion2.get(1)).as("Region entry has not been saved").isNotNull();
        Assertions.assertThat(partitionedRegion2.get(1)).as("Region entry has not been saved correctly").isEqualTo(2);
        RegionUtils.clearRegion(partitionedRegion2);
        Assertions.assertThat(partitionedRegion2.get(1)).as("Region entry has not been deleted").isNull();
        Assertions.assertThat(partitionedRegion2.keySetOnServer().size()).as("Region has not been cleaned").isEqualTo(0);
    }

    /**
     * Starts a client.
     * @throws java.io.IOException
     */
    private void startClient() throws IOException {
        PropertiesHelper properties = new PropertiesHelper("/regionClearingProperties.properties");

        cache = new ClientCacheFactory()
                .set("cache-xml-file", "regionClearingTestClientCache.xml")
                .set("log-level", properties.getStringProperty("log-level"))
                .set("license-file", properties.getStringProperty("license-file"))
                .set("license-type", properties.getStringProperty("license-type"))
                .create();

        replicatedRegion1 = cache.getRegion("replicated_region_1");
        replicatedRegion1.registerInterestRegex(".*", InterestResultPolicy.KEYS_VALUES, false, true);
        partitionedRegion1 = cache.getRegion("partitioned_region_1");
        partitionedRegion1.registerInterestRegex(".*", InterestResultPolicy.KEYS_VALUES, false, true);

        replicatedRegion2 = cache.getRegion("replicated_region_2");
        replicatedRegion2.registerInterestRegex(".*", InterestResultPolicy.KEYS_VALUES, false, true);
        partitionedRegion2 = cache.getRegion("partitioned_region_2");
        partitionedRegion2.registerInterestRegex(".*", InterestResultPolicy.KEYS_VALUES, false, true);
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
                null);
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
