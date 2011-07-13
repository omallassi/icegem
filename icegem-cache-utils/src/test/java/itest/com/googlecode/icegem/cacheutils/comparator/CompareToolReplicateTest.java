package itest.com.googlecode.icegem.cacheutils.comparator;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
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
import com.googlecode.icegem.cacheutils.Launcher;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.ServerTemplate;

public class CompareToolReplicateTest {
	private static final int NUMBER_OF_ENTRIES = 1000;
	private static final int NUMBER_OF_ENTRIES_PERFORMANCE = 100000;

	private static final String REGION_NAME = "data";
	
	/** Field cacheServer1 */
	private static Process cacheServer1;
	/** Field cacheServer2 */
	private static Process cacheServer2;
	/** Field javaProcessLauncher */
	/** Field cacheServer3 */
	private static Process cacheServer3;
	/** Field cacheServer4 */
	private static Process cacheServer4;
	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher(
		true, true, false);
	private static PropertiesHelper cluster40804PropertiesHelper;
	private static PropertiesHelper cluster40805PropertiesHelper;

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException,
		TimeoutException {
		startCacheServers();

		cluster40804PropertiesHelper = new PropertiesHelper(
			"/compareToolReplicateServerProperties40804.properties");

		cluster40805PropertiesHelper = new PropertiesHelper(
			"/compareToolReplicateServerProperties40805.properties");
	}

	@Before
	public void beforeMethod() {
		fillData(
			cluster40804PropertiesHelper.getStringProperty("start-locator"),
			REGION_NAME, NUMBER_OF_ENTRIES);
		fillData(
			cluster40805PropertiesHelper.getStringProperty("start-locator"),
			REGION_NAME, NUMBER_OF_ENTRIES);
	}

	@AfterClass
	public static void tearDown() throws IOException, InterruptedException {
		stopCacheServers();
	}

	private void fillData(String locator, String regionName, int numberOfEntries) {
		String host = locator.substring(0, locator.indexOf("["));
		int port = Integer.parseInt(locator.substring(locator.indexOf("[") + 1,
			locator.indexOf("]")));

		ClientCache clientCache = new ClientCacheFactory()
			.addPoolLocator(host, port).set("log-level", "none").create();

		ClientRegionFactory<Long, User> clientRegionFactory = clientCache
			.createClientRegionFactory(ClientRegionShortcut.PROXY);

		Region<Long, User> region = clientRegionFactory.create(regionName);

		for (int i = 1; i <= numberOfEntries; i++) {
			if ((i % 10000) == 0) {
				System.out.println("Region \"" + regionName
					+ "\" of cluster \""+locator+"\": Filling record #" + i);
			}
			region.put(new Long(i), new User(i, "Ivan " + i + "th", i % 100,
				i % 2 == 0));
		}

		clientCache.close();
	}

	private void changeData(String locator, String regionName) {
		String host = locator.substring(0, locator.indexOf("["));
		int port = Integer.parseInt(locator.substring(locator.indexOf("[") + 1,
			locator.indexOf("]")));

		ClientCache clientCache = new ClientCacheFactory()
			.addPoolLocator(host, port).set("log-level", "none").create();

		ClientRegionFactory<Long, User> clientRegionFactory = clientCache
			.createClientRegionFactory(ClientRegionShortcut.PROXY);

		Region<Long, User> region = clientRegionFactory.create(regionName);

		region.put(new Long(1), new User(2, "Ivan 2nd", 2, true));

		clientCache.close();
	}

	@Test
	public void testMainPositive() throws FileNotFoundException, IOException,
		InterruptedException {
		System.out.println("testMainPositive");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		long startTime = System.currentTimeMillis();
		int exitCode = javaProcessLauncher
			.runAndWaitProcessExitCode(
				Launcher.class,
				vmArguments,
				new String[] {
					"compare",
					"-sr",
					REGION_NAME,
					"-tr",
					REGION_NAME,
					"-sl",
					cluster40804PropertiesHelper
						.getStringProperty("start-locator"),
					"-tl",
					cluster40805PropertiesHelper
						.getStringProperty("start-locator"),
						"-c", "itest.com.googlecode.icegem.cacheutils.comparator" });

		long finishTime = System.currentTimeMillis();

		System.out.println("Compared in " + (finishTime - startTime) + "ms");

		assertEquals(0, exitCode);
	}

	@Test
	public void testMainPositivePerformance() throws FileNotFoundException,
		IOException, InterruptedException {
		System.out.println("testMainPositivePerformance");

		fillData(
			cluster40804PropertiesHelper.getStringProperty("start-locator"),
			REGION_NAME, NUMBER_OF_ENTRIES_PERFORMANCE);
		fillData(
			cluster40805PropertiesHelper.getStringProperty("start-locator"),
			REGION_NAME, NUMBER_OF_ENTRIES_PERFORMANCE);

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		long startTime = System.currentTimeMillis();
		int exitCode = javaProcessLauncher
			.runAndWaitProcessExitCode(
				Launcher.class,
				vmArguments,
				new String[] {
					"compare",
					"-sr",
					REGION_NAME,
					"-tr",
					REGION_NAME,
					"-sl",
					cluster40804PropertiesHelper
						.getStringProperty("start-locator"),
					"-tl",
					cluster40805PropertiesHelper
						.getStringProperty("start-locator"), "-lf", "80",
						"-c", "itest.com.googlecode.icegem.cacheutils.comparator" });

		long finishTime = System.currentTimeMillis();

		System.out.println("Compared in " + (finishTime - startTime) + "ms");

		assertEquals(0, exitCode);
	}

	@Test
	public void testMainNegative() throws FileNotFoundException, IOException,
		InterruptedException {
		System.out.println("testMainNegative");

		changeData(
			cluster40804PropertiesHelper.getStringProperty("start-locator"),
			REGION_NAME);

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		long startTime = System.currentTimeMillis();
		int exitCode = javaProcessLauncher
			.runAndWaitProcessExitCode(
				Launcher.class,
				vmArguments,
				new String[] {
					"compare",
					"-sr",
					REGION_NAME,
					"-tr",
					REGION_NAME,
					"-sl",
					cluster40804PropertiesHelper
						.getStringProperty("start-locator"),
					"-tl",
					cluster40805PropertiesHelper
						.getStringProperty("start-locator"),
						"-c", "itest.com.googlecode.icegem.cacheutils.comparator" });

		long finishTime = System.currentTimeMillis();

		System.out.println("Compared in " + (finishTime - startTime) + "ms");

		assertEquals(1, exitCode);
	}

	private static void startCacheServers() throws IOException, InterruptedException {
		cacheServer1 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=compareToolReplicateServerProperties40804.properties" },
				null);
		cacheServer2 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=compareToolReplicateServerProperties40805.properties" },
				null);
		cacheServer3 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=compareToolReplicateServerProperties40814.properties" },
				null);
		cacheServer4 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=compareToolReplicateServerProperties40815.properties" },
				null);
	}

	private static void stopCacheServers() throws IOException, InterruptedException {
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer3);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer4);
	}
}
