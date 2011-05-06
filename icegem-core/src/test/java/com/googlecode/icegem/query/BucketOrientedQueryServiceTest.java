package com.googlecode.icegem.query;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.query.QueryException;
import com.gemstone.gemfire.cache.query.SelectResults;
import com.gemstone.gemfire.cache.query.Struct;
import org.fest.assertions.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * TODO: Starts two servers for tests automatically.
 * TODO: At that time we need to start two servers before start tests.
 *
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


    @BeforeClass
    public void setUp() throws IOException {
//        startCacheServer();
        startClient();
        populateRegionByPersons(data);
    }

    @AfterClass
    public void tearDown() {
        cache.close();
    }

    @Test
    public void testBucketDataRetrieveForExistedAndFakeKeys() throws QueryException {
        SelectResults<Object> resultsBasedOnExistedKey = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1)){});
        Assertions.assertThat(resultsBasedOnExistedKey.size()).as("Wrong number of entries from the same bucket was found for key = 1").isEqualTo(10);
        Assertions.assertThat(containsPersonWithSocialNumber(resultsBasedOnExistedKey, 1)).as("Entry with key = 1 doesn't exist in results").isTrue();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsBasedOnExistedKey, 11)).as("Entry with key = 11 doesn't exist in results").isTrue();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsBasedOnExistedKey, 2)).as("Entry with key = 2 exists in results but it should not").isFalse();

        SelectResults<Object> resultsBasedOnFakeKey = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(101)){});
        Assertions.assertThat(resultsBasedOnFakeKey.size()).as("Wrong number of entries from the same bucket was found for key = 1").isEqualTo(10);
        Assertions.assertThat(containsPersonWithSocialNumber(resultsBasedOnFakeKey, 101)).as("Entry with fake key = 101 exists in results").isFalse();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsBasedOnFakeKey, 1)).as("Entry with key = 1 doesn't exist in results").isTrue();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsBasedOnFakeKey, 2)).as("Entry with key = 2 exists in results but it should not").isFalse();
        Assertions.assertThat(resultsBasedOnFakeKey.equals(resultsBasedOnExistedKey)).as("Results based on fake key and existed key for one bucket are not the same").isFalse();
    }

    @Test
    public void testBucketsDataRetrieve() throws QueryException {
        SelectResults<Object> resultsFromOneBucket = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1)){});
        Assertions.assertThat(resultsFromOneBucket.size()).as("Wrong number of entries from the same bucket was found for key = 1").isEqualTo(10);
        Assertions.assertThat(containsPersonWithSocialNumber(resultsFromOneBucket, 1)).as("Entry with key = 1 doesn't exist in results").isTrue();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsFromOneBucket, 2)).as("Entry with key = 2 exists in results but it should not").isFalse();

        SelectResults<Object> resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1, 2)){});
        Assertions.assertThat(resultsFromTwoBuckets.size()).as("Wrong number of entries from the buckets was found for keys: [1, 2]").isEqualTo(20);
        Assertions.assertThat(containsPersonWithSocialNumber(resultsFromTwoBuckets, 1)).as("Entry with key = 1 doesn't exist in results").isTrue();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsFromTwoBuckets, 2)).as("Entry with key = 2 doesn't exist in results").isTrue();

        resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data", data, new HashSet<Object>(Arrays.asList(1, 11, 2)){});
        Assertions.assertThat(resultsFromTwoBuckets.size()).as("Wrong number of entries from the buckets was found for keys: [1, 11, 2]").isEqualTo(20);
        Assertions.assertThat(containsPersonWithSocialNumber(resultsFromTwoBuckets, 1)).as("Entry with key = 1 doesn't exist in results").isTrue();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsFromTwoBuckets, 11)).as("Entry with key = 11 doesn't exist in results").isTrue();
        Assertions.assertThat(containsPersonWithSocialNumber(resultsFromTwoBuckets, 2)).as("Entry with key = 2 doesn't exist in results").isTrue();
    }

    @Test
    public void testBucketDataRetrieveUsingQueryLimit() throws QueryException {
        SelectResults<Object> resultsFromOneBucket = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data limit 3", data, new HashSet<Object>(Arrays.asList(1)){});
        Assertions.assertThat(resultsFromOneBucket.size()).as("Wrong number of entries from the same bucket was found for key = 1 and limit = 3").isEqualTo(3);

        SelectResults<Object> resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data limit 3", data, new HashSet<Object>(Arrays.asList(1, 2)){});
        Assertions.assertThat(resultsFromTwoBuckets.size()).as("Wrong number of entries from the buckets was found for keys: [1, 2] and limit = 3").isEqualTo(3);

        resultsFromTwoBuckets = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data LIMIT 3", data, new HashSet<Object>(Arrays.asList(1, 2)){});
        Assertions.assertThat(resultsFromTwoBuckets.size()).as("Wrong number of entries from the buckets was found for keys: [1, 2] and LIMIT = 3").isEqualTo(3);
    }

    @Test
    public void testComplexQuering() throws QueryException {
        SelectResults<Object> results = BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)){});
        Assertions.assertThat(results.size()).as("Query parameters doesn't work correct").isEqualTo(1);
        Person person = (Person) data.get(1);
        Assertions.assertThat(results.asList().get(0).equals(person)).as("Wrong query result").isTrue();
        Assertions.assertThat(results.getCollectionType().getElementType().resolveClass()).as("Wrong element type").isEqualTo(Object.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT children FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)){});
        Assertions.assertThat(results.size()).as("Wrong query result for object's nested collection").isEqualTo(1);
        Assertions.assertThat(results.asList().get(0).equals(person.getChildren())).as("Wrong query result for object's nested collection").isTrue();
        Assertions.assertThat(results.getCollectionType().getElementType().resolveClass()).as("Wrong element type").isEqualTo(Object.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT socialNumber FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)){});
        Assertions.assertThat(results.size()).as("Wrong query result for object's field").isEqualTo(1);
        Assertions.assertThat(results.asList().get(0).equals(1)).as("Wrong query result for object's field").isTrue();
        Assertions.assertThat(results.getCollectionType().getElementType().resolveClass()).as("Wrong element type").isEqualTo(Object.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT socialNumber, children FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(1)){});
        Assertions.assertThat(results.size()).as("Wrong query result for structures").isEqualTo(1);
        Assertions.assertThat(results.getCollectionType().getElementType().resolveClass()).as("Wrong element type").isEqualTo(Struct.class);

        results = BucketOrientedQueryService.executeOnBuckets("SELECT socialNumber, children FROM /data WHERE socialNumber = $1",
                new Object[]{1}, data, new HashSet<Object>(Arrays.asList(2)){});
        Assertions.assertThat(results.size()).as("Wrong query result for structures").isEqualTo(0);
        Assertions.assertThat(results.getCollectionType().getElementType().resolveClass()).as("Wrong element type").isEqualTo(Struct.class);
    }

    @Test(expectedExceptions = QueryException.class)
    public void testExecutionWithEmptyQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets("", data, new HashSet<Object>(Arrays.asList(1)){});
    }

    @Test(expectedExceptions = QueryException.class)
    public void testExecutionWithWrongQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets("SELECT *", data, new HashSet<Object>(Arrays.asList(1)){});
    }

    @Test(expectedExceptions = QueryException.class)
    public void testExecutionWithNullQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets(null, data, new HashSet<Object>(Arrays.asList(1)){});
    }

    @Test(expectedExceptions = QueryException.class)
    public void testExecutionWithNotExistedRegionQueryString() throws QueryException {
        BucketOrientedQueryService.executeOnBuckets("SELECT * FROM /data1", data, new HashSet<Object>(Arrays.asList(1)){});
    }

    private void startClient() {
        ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolLocator("localhost", LOCATOR_PORT);
        cache = clientCacheFactory.set("log-level", "warning").create();
        ClientRegionFactory<Object, Object> regionFactory =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        data = regionFactory.create("data");
    }

    private void startCacheServer() throws IOException {
        AttributesFactory attributesFactory = new AttributesFactory();
        attributesFactory.setDataPolicy(DataPolicy.PARTITION);
        RegionAttributes regionAttributes = attributesFactory.create();

        CacheFactory cacheFactory = new CacheFactory();
        Cache cache = cacheFactory.set("mcast-port", "0").set("log-level", "warning").set("start-locator" , "localhost[" + LOCATOR_PORT + "]").create();
        Region<Object, Object> data = cache.createRegionFactory(regionAttributes).create("data");
    }

    /**
     * Checks existence of person with specified social number in results.
     *
     * @param results of type SelectResults<Object>
     * @param socialNumber of type int
     * @return boolean
     */
    private static boolean containsPersonWithSocialNumber(SelectResults<Object> results, int socialNumber) {
        for (Object result : results) {
            if (((Person) result).getSocialNumber() == socialNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * Populates region by persons.
     *
     * @param region of type Region<Object, Object>
     */
    private static void populateRegionByPersons(Region<Object, Object> region) {
        for (int i = 1; i <= 100; i++) {
            region.put(i, new Person(i, Arrays.asList(Integer.toString(i), Integer.toString(i*2))));
        }
    }
}
