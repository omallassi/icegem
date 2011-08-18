package itest.com.googlecode.icegem.utils;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.utils.CacheUtils;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 * TODO: need to implement abstract integration test in order to avoid
 * code duplicates all over the place in the tests.
 * 
 * Tests for {@link CacheUtils} class. 
 */
public class CacheUtilsTest {
    /** */
    private static final String PROP_FILE = "cacheUtilsTestServerProperties.properties";

    /** Locator port. */
    private static final int LOCATOR_PORT = 10355;

    /** Cache. */
    private static ClientCache cache;

    /** Partitioned region. */
    private static Region<Object, Object> partitionedRegion;

    /** Replicated region. */
    private static Region<Object, Object> replicatedRegion;

    /** Cache server 1. */
    private static Process cacheServer1;

    /** Cache server 2. */
    private static Process cacheServer2;

    /** Java process launcher. */
    private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException, TimeoutException {
	startCacheServers();

	startClient();

	CacheUtils.clearRegion(partitionedRegion);
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
	cache.close();

	stopCacheServers();
    }

    @Before
    public void after() throws InterruptedException, IOException {
	CacheUtils.clearRegion(partitionedRegion);
	CacheUtils.clearRegion(replicatedRegion);
    }

    /**
     * Starts a client.
     * 
     * @throws java.io.IOException
     */
    private static void startClient() throws IOException {
	ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolLocator("localhost", LOCATOR_PORT);

	PropertiesHelper properties = new PropertiesHelper("/" + PROP_FILE);

	cache = clientCacheFactory.set("log-level", properties.getStringProperty("log-level"))
		.set("license-file", properties.getStringProperty("license-file"))
		.set("license-type", properties.getStringProperty("license-type")).create();

	ClientRegionFactory<Object, Object> regionFactory = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

	partitionedRegion = regionFactory.create("partitioned_region");
	replicatedRegion = regionFactory.create("replicated_region");
    }

    /**
     * Starts two cache servers for tests.
     * 
     * @throws IOException
     *             when
     * @throws InterruptedException
     *             when
     */
    private static void startCacheServers() throws IOException, InterruptedException {
	String[] javaArgs = new String[] { "-DgemfirePropertyFile=" + PROP_FILE };

	cacheServer1 = javaProcessLauncher.runWithConfirmation(ServerTemplate.class, javaArgs, null);
	cacheServer2 = javaProcessLauncher.runWithConfirmation(ServerTemplate.class, javaArgs, null);
    }

    /**
     * Stops cache servers.
     * 
     * @throws IOException
     *             when
     * @throws InterruptedException
     */
    private static void stopCacheServers() throws IOException, InterruptedException {
	javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
	javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
    }

    /**
     * JUnit.
     */
    @Test
    public void testClearRegion() {
	/**
	 * TODO: This test is implemented in {@link RegionClearingClientTest}.
	 * 	Is it better to place it here? 
	 */
    }

    /**
     * JUnit.
     */
    @Test
    public void testPartitionedRegionSize() {
	assertEquals(0, CacheUtils.getRegionSize(partitionedRegion));
	
	int keyCnt = 1000;
	
	for (int i = 0; i < keyCnt; i++)
	    partitionedRegion.put(i, "Value" + i);
	
	assertEquals(keyCnt, CacheUtils.getRegionSize(partitionedRegion));
    }
    
    /**
     * JUnit.
     */
    @Test
    public void testReplicatedRegionSize() {
	assertEquals(0, CacheUtils.getRegionSize(replicatedRegion));

	int keyCnt = 1000;
	
	for (int i = 0; i < keyCnt; i++)
	    replicatedRegion.put(i, "Value" + i);
	
	assertEquals(keyCnt, CacheUtils.getRegionSize(replicatedRegion));
    }
    
    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffNoRetry() {
	//fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffSingeRetry() {
	//fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffNoSuccess() {
	//fail("Not yet implemented");
    }
}
