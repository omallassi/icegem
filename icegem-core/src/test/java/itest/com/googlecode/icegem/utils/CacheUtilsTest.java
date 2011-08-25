package itest.com.googlecode.icegem.utils;

import static com.gemstone.gemfire.cache.client.ClientRegionShortcut.PROXY;
import static junit.framework.Assert.assertEquals;
import itest.com.googlecode.icegem.AbstractIntegrationTest;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.utils.CacheUtils;

/**
 * Tests for {@link CacheUtils} class. 
 */
public class CacheUtilsTest extends AbstractIntegrationTest {
    /** GemFire property file name. */
    private static final String PROP_FILE = "cacheUtilsTestServerProperties.properties";

    /** Partitioned region. */
    private static Region<Object, Object> partitionedRegion;

    /** Replicated region. */
    private static Region<Object, Object> replicatedRegion;

    @BeforeClass
    public static void beforeAllTests() throws Exception {
	startCacheServersAndClient(2, PROP_FILE);
	
	partitionedRegion = createClientRegion("partitioned_region", PROXY);
	replicatedRegion = createClientRegion("replicated_region", PROXY);
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
	stopCacheServersAndClient();
    }

    @After
    public void afterTest() throws InterruptedException, IOException {
	clearClientRegions();
    }

    /**
     * JUnit.
     */
    @Test
    public void testPartitionedRegionSize() {
	assertEquals(0, CacheUtils.getRegionSize(partitionedRegion));

	int keyCnt = 100;

	for (int i = 0; i < keyCnt; i++) {
	    partitionedRegion.put(i, "Value" + i);

	    if (i % 50 == 0)
		info("Stored key-value pairs: " + i);
	}

	assertEquals(keyCnt, CacheUtils.getRegionSize(partitionedRegion));
    }

    /**
     * JUnit.
     */
    @Test
    public void testReplicatedRegionSize() {
	assertEquals(0, CacheUtils.getRegionSize(replicatedRegion));

	int keyCnt = 100;

	for (int i = 0; i < keyCnt; i++) {
	    replicatedRegion.put(i, "Value" + i);

	    if (i % 50 == 0)
		info("Stored key-value pairs: " + i);
	}

	assertEquals(keyCnt, CacheUtils.getRegionSize(replicatedRegion));
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
    public void testRetryWithExponentialBackoffNoRetry() {
	// fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffSingeRetry() {
	// fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffNoSuccess() {
	// fail("Not yet implemented");
    }
}
