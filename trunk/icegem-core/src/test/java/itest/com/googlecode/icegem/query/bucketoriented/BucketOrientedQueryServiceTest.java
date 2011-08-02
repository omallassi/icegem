package itest.com.googlecode.icegem.query.bucketoriented;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import itest.com.googlecode.icegem.query.common.model.Person;
import itest.com.googlecode.icegem.query.common.utils.PersonUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.query.QueryException;
import com.gemstone.gemfire.cache.query.SelectResults;
import com.gemstone.gemfire.cache.query.Struct;
import com.googlecode.icegem.query.bucketoriented.BucketOrientedQueryService;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 * Tests for bucket oriented query service.
 *
 * @author Andrey Stepanov aka standy
 */
public class BucketOrientedQueryServiceTest {
    /** Field LOCATOR_PORT  */
    private static final int LOCATOR_PORT = 10355;
    /** Field cache  */
    private static ClientCache cache;
    /** Field data  */
    private static Region<Object, Object> data;
    /** Field cacheServer1  */
    private static Process cacheServer1;
    /** Field cacheServer2  */
    private static Process cacheServer2;
    /** Field javaProcessLauncher  */
    private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher(true, true, true);

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException, TimeoutException {
        startCacheServers();
        startClient();
        PersonUtils.populateRegionByPersons(data, 100);
    }

    @AfterClass
    public static void tearDown() throws IOException, InterruptedException {
        cache.close();
        stopCacheServers();
    }

    @Test
    public void testBucketDataRetrieveForExistedAndFakeKeys() throws QueryException {
        SelectResults<Object> resultsBasedOnExistedKey = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1)));
        checkResults(resultsBasedOnExistedKey, 10, new int[] {1, 11}, new int[] {2});

        SelectResults<Object> resultsBasedOnFakeKey = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(101)));
        checkResults(resultsBasedOnFakeKey , 10, new int[] {1}, new int[] {101, 2});
        assertFalse(resultsBasedOnFakeKey.equals(resultsBasedOnExistedKey));
    }

	protected void checkResults(SelectResults<Object> resultsBasedOnExistedKey, int size, int[] exist, int[] notExist) {
		assertEquals(size, resultsBasedOnExistedKey.size());
		for(int i = 0 ; i < exist.length; i++) {
	        assertTrue(PersonUtils.containsPersonWithSocialNumber(resultsBasedOnExistedKey.asList(), exist[i]));
		}
		for(int i = 0 ; i < notExist.length; i++) {
	        assertFalse(PersonUtils.containsPersonWithSocialNumber(resultsBasedOnExistedKey.asList(), notExist[i]));
		}
	}

    @Test
    public void testBucketsDataRetrieve() throws QueryException, InterruptedException {
//        SelectResults<Object> resultsFromOneBucket = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1)));
//        checkResults(resultsFromOneBucket , 10, new int[] {1}, new int[] {2});

        SelectResults<Object> resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1, 2)));
        checkResults(resultsFromTwoBuckets, 20, new int[] {1, 2}, new int[0]);

//        resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1, 11, 2)));
//        checkResults(resultsFromTwoBuckets, 20, new int[] {1, 2, 11}, new int[0]);
    }

    @Test
    public void testBucketDataRetrieveUsingQueryLimit() throws QueryException {
        SelectResults<Object> resultsFromOneBucket = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data limit 3", data, new HashSet<Object>(Arrays.asList(1)));
        checkResults(resultsFromOneBucket, 3, new int[0], new int[0]);

        SelectResults<Object> resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data limit 3", data, new HashSet<Object>(Arrays.asList(1, 2)));
        checkResults(resultsFromTwoBuckets, 3, new int[0], new int[0]);

        resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data LIMIT 21", data, new HashSet<Object>(Arrays.asList(1, 2)){});
        checkResults(resultsFromTwoBuckets, 20, new int[0], new int[0]);
    }

    @Test
    public void testComplexQuering() throws QueryException {
        SelectResults<Object> results = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)));
        assertEquals(results.size(), 1);
        Person person = (Person) data.get(1);
        assertEquals(results.asList().get(0).equals(person), true);
        assertEquals(results.getCollectionType().getElementType().resolveClass(), Object.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT children FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)));
        assertEquals(results.size(), 1);
        assertEquals(results.asList().get(0).equals(person.getChildren()), true);
        assertEquals(results.getCollectionType().getElementType().resolveClass(), Object.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT socialNumber FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)));
        assertEquals(results.size(), 1);
        assertEquals(results.asList().get(0).equals(1), true);
        assertEquals(results.getCollectionType().getElementType().resolveClass(), Object.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT socialNumber, children FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)));
        assertEquals(results.size(), 1);
        assertEquals(results.getCollectionType().getElementType().resolveClass(), Struct.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT socialNumber, children FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(2)));
        assertEquals(results.size(), 0);
        assertEquals(results.getCollectionType().getElementType().resolveClass(), Struct.class);
    }

    @Test(expected = QueryException.class)
    public void testExecutionWithEmptyQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets("", data, new HashSet<Object>(Arrays.asList(1)));
    }

    @Test(expected = QueryException.class)
    public void testExecutionWithWrongQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets("SELECT *", data, new HashSet<Object>(Arrays.asList(1)));
    }

    @Test(expected = QueryException.class)
    public void testExecutionWithNullQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets(null, data, new HashSet<Object>(Arrays.asList(1)));
    }

    @Test(expected = QueryException.class)
    public void testExecutionWithNotExistedRegionQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data1", data, new HashSet<Object>(Arrays.asList(1)));
    }

    /**
     * Starts a client.
     * @throws java.io.IOException
     */
    private static void startClient() throws IOException {
        ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolLocator("localhost", LOCATOR_PORT);

        PropertiesHelper properties = new PropertiesHelper("/bucketOrientedServerProperties.properties");

        cache = clientCacheFactory
                .set("log-level", properties.getStringProperty("log-level"))
                .set("license-file", properties.getStringProperty("license-file"))
                .set("license-type", properties.getStringProperty("license-type"))
                .create();

        ClientRegionFactory<Object, Object> regionFactory =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        data = regionFactory.create("data");
    }

    /**
     * Starts two cache servers for tests.
     *
     * @throws IOException when
     * @throws InterruptedException when
     */
    private static void startCacheServers() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=bucketOrientedServerProperties.properties"}, null);
        cacheServer2 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=bucketOrientedServerProperties.properties"}, null);
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
