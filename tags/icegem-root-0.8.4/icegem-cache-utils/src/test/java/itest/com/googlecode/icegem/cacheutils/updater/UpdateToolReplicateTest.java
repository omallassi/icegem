package itest.com.googlecode.icegem.cacheutils.updater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.Region.Entry;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.cacheutils.Launcher;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.ServerTemplate;

public class UpdateToolReplicateTest {
	private static final String REGION_DATA1 = "data1";
	private static final String REGION_DATA2 = "data2";
	private static final String REGION_DATA1_SR1 = "data1/sr1";

	private static final String KEY = "key";
	private static final String KEY_AS1 = "key-as1";
	private static final String KEY_AS2 = "key-as2";

	private static final String VALUE = "value";

	/** Field cacheServer1 */
	private static Process cacheServer1;
	/** Field cacheServer2 */
	private static Process cacheServer2;
	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher(
		false, false, false);

	private static ClientRegionFactory<Object, Object> clientRegionFactory;
	private static Map<String, Region<Object, Object>> nameToRegionMap = new HashMap<String, Region<Object, Object>>();
	private static ClientCache clientCache;
	private static PropertiesHelper propertiesHelper;

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException,
		TimeoutException {

		propertiesHelper = new PropertiesHelper(
			"/updateToolReplicateServerProperties41414.properties");

		startCacheServers();

		createRegions(new String[] { REGION_DATA1, REGION_DATA2,
			REGION_DATA1_SR1 });

		put(REGION_DATA1, KEY, VALUE);
		put(REGION_DATA2, KEY, VALUE);
		put(REGION_DATA1_SR1, KEY, VALUE);
	}

	@AfterClass
	public static void tearDown() throws IOException, InterruptedException {
		clientCache.close();
		stopCacheServers();
	}

	private static void createRegions(String[] regionNames) {
		clientCache = new ClientCacheFactory()
			.addPoolLocator("localhost", 10355).set("log-level", "none")
			.setPoolSubscriptionEnabled(true).create();

		clientRegionFactory = clientCache
			.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);

		clientRegionFactory.setStatisticsEnabled(true);

		Region<Object, Object> region = null;
		for (String regionName : regionNames) {
			String[] regionNameParts = regionName.split("/");
			for (int i = 0; i < regionNameParts.length; i++) {
				String regionNamePart = regionNameParts[i];
				if (i == 0) {
					region = clientCache.getRegion(regionNamePart);

					if (region == null) {
						region = clientRegionFactory.create(regionNamePart);
					}
				} else {
					nameToRegionMap.get(regionNamePart);
					Region<Object, Object> subregion = region
						.getSubregion(regionNamePart);

					if (subregion == null) {
						subregion = region.createSubregion(regionNamePart,
							region.getAttributes());
					}

					region = subregion;
				}
			}

			region.registerInterestRegex(".*");

			nameToRegionMap.put(regionName, region);
		}
	}

	private static void put(String regionName, Object key, Object value) {
		Region<Object, Object> region = nameToRegionMap.get(regionName);
		region.put(key, value);
	}

	private Entry<Object, Object> get(String regionName, Object key) {
		Region<Object, Object> region = nameToRegionMap.get(regionName);
		return region.getEntry(key);
	}

	private void destroy(String regionName, Object key) {
		Region<Object, Object> region = nameToRegionMap.get(regionName);
		region.destroy(key);
	}

	@Test
	public void testMainPositiveOneRegion() throws Exception {
		System.out.println("testMainPositiveOneRegion");

		long updateStartTime = System.currentTimeMillis();

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "update", "-l",
				propertiesHelper.getStringProperty("locators"), "-s",
				"localhost[41414]", "-r", REGION_DATA1 });

		long lastModifiedTime = get(REGION_DATA1, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTime>updateStartTime);

		assertEquals(exitCode, 0);
	}

	@Test
	public void testMainPositiveOneRegionWithSubregions() throws Exception {
		System.out.println("testMainPositiveOneRegionWithSubregions");

		long updateStartTime = System.currentTimeMillis();

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "update", "-l",
				propertiesHelper.getStringProperty("locators"), "-s",
				"localhost[41414]", "-r", REGION_DATA1, "-c" });

		long lastModifiedTimeForData1 = get(REGION_DATA1, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1>updateStartTime);

		long lastModifiedTimeForData1Sr1 = get(REGION_DATA1_SR1, KEY)
			.getStatistics().getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1Sr1>updateStartTime);

		assertEquals(exitCode, 0);
	}

	@Test
	public void testMainPositiveTwoRegionsWithoutSubregions() throws Exception {
		System.out.println("testMainPositiveTwoRegionsWithoutSubregions");

		long updateStartTime = System.currentTimeMillis();

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "update", "-l",
				propertiesHelper.getStringProperty("locators"), "-s",
				"localhost[41414]", "-r", REGION_DATA1 + "," + REGION_DATA2 });

		long lastModifiedTimeForData1 = get(REGION_DATA1, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1>updateStartTime);

		long lastModifiedTimeForData2 = get(REGION_DATA2, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTimeForData2>updateStartTime);

		long lastModifiedTimeForData1Sr1 = get(REGION_DATA1_SR1, KEY)
			.getStatistics().getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1Sr1 < updateStartTime);

		assertEquals(exitCode, 0);
	}

	@Test
	public void testMainPositiveAllRegions() throws Exception {
		System.out.println("testMainPositiveAllRegions");

		long updateStartTime = System.currentTimeMillis();

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "update", "-l",
				propertiesHelper.getStringProperty("locators"), "-s",
				"localhost[41414]", "-a" });

		long lastModifiedTimeForData1 = get(REGION_DATA1, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1>updateStartTime);

		long lastModifiedTimeForData2 = get(REGION_DATA2, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTimeForData2>updateStartTime);

		long lastModifiedTimeForData1Sr1 = get(REGION_DATA1_SR1, KEY)
			.getStatistics().getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1Sr1<updateStartTime);

		assertEquals(exitCode, 0);
	}

	@Test
	public void testMainPositiveAllRegionsWithPackages() throws Exception {
		System.out.println("testMainPositiveAllRegionsWithPackages");

		HierarchyRegistry
			.registerAll(
				UpdateToolReplicateTest.class.getClassLoader(),
				itest.com.googlecode.icegem.cacheutils.updater.as1.SimpleClass.class,
				itest.com.googlecode.icegem.cacheutils.updater.as2.SimpleClass.class);

		put(REGION_DATA1,
			KEY_AS1,
			new itest.com.googlecode.icegem.cacheutils.updater.as1.SimpleClass());
		put(REGION_DATA2,
			KEY_AS2,
			new itest.com.googlecode.icegem.cacheutils.updater.as2.SimpleClass());

		long updateStartTime = System.currentTimeMillis();

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] {
				"update",
				"-l",
				propertiesHelper.getStringProperty("locators"),
				"-s",
				"localhost[41414]",
				"-a",
				"-p",
				"itest.com.googlecode.icegem.cacheutils.updater.as1,"
					+ "itest.com.googlecode.icegem.cacheutils.updater.as2" });

		long lastModifiedTimeForData1 = get(REGION_DATA1, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1>updateStartTime);

		long lastModifiedTimeForAs1 = get(REGION_DATA1, KEY_AS1)
			.getStatistics().getLastModifiedTime();
		assertTrue(lastModifiedTimeForAs1>updateStartTime);

		long lastModifiedTimeForData2 = get(REGION_DATA2, KEY).getStatistics()
			.getLastModifiedTime();
		assertTrue(lastModifiedTimeForData2>updateStartTime);

		long lastModifiedTimeForAs2 = get(REGION_DATA2, KEY_AS2)
			.getStatistics().getLastModifiedTime();
		assertTrue(lastModifiedTimeForAs2>updateStartTime);

		long lastModifiedTimeForData1Sr1 = get(REGION_DATA1_SR1, KEY)
			.getStatistics().getLastModifiedTime();
		assertTrue(lastModifiedTimeForData1Sr1<updateStartTime);

		assertEquals(exitCode, 0);

		destroy(REGION_DATA1, KEY_AS1);
		destroy(REGION_DATA2, KEY_AS2);
	}

	private static void startCacheServers() throws IOException, InterruptedException {
		cacheServer1 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=updateToolReplicateServerProperties41414.properties" },
				null);
		cacheServer2 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=updateToolReplicateServerProperties41415.properties" },
				null);
	}

	private static void stopCacheServers() throws IOException, InterruptedException {
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
	}
}
