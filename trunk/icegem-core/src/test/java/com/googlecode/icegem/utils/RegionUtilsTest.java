package com.googlecode.icegem.utils;

import static junit.framework.Assert.fail;

import java.io.IOException;
import java.util.List;
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
import com.googlecode.icegem.query.pagination.PageKey;

/**
 * TODO: need to implement abstract integration test in order to avoid
 * code duplicates all over the place in the tests.
 * 
 * Tests for {@link CacheUtils} class. 
 */
public class RegionUtilsTest {
    /** Field LOCATOR_PORT */
    private static final int LOCATOR_PORT = 10355;

    /** Field cache */
    private static ClientCache cache;

    /** Region for querying */
    private static Region data;

    /** Field cacheServer1 */
    private static Process cacheServer1;

    /** Field cacheServer2 */
    private static Process cacheServer2;

    /** Field javaProcessLauncher */
    private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException, TimeoutException {
	startCacheServers();

	startClient();

	CacheUtils.clearRegion(data);
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
	cache.close();

	stopCacheServers();
    }

    @Before
    public void after() throws InterruptedException, IOException {
	CacheUtils.clearRegion(data);
    }

    /**
     * Starts a client.
     * 
     * @throws java.io.IOException
     */
    private static void startClient() throws IOException {
	ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolLocator("localhost", LOCATOR_PORT);

	PropertiesHelper properties = new PropertiesHelper("/paginatedQueryServerProperties.properties");

	cache = clientCacheFactory.set("log-level", properties.getStringProperty("log-level"))
		.set("license-file", properties.getStringProperty("license-file"))
		.set("license-type", properties.getStringProperty("license-type")).create();

	ClientRegionFactory<Object, Object> regionFactory = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

	data = regionFactory.create("data");

	ClientRegionFactory<PageKey, List<Object>> regionFactoryForHelpRegion = cache
		.createClientRegionFactory(ClientRegionShortcut.PROXY);
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
	String[] javaArgs = new String[] { "-DgemfirePropertyFile=paginatedQueryServerProperties.properties" };

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
	fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRegionSize() {

    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffNoRetry() {
	fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffSingeRetry() {
	fail("Not yet implemented");

    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffNoSuccess() {
	fail("Not yet implemented");
    }
}
