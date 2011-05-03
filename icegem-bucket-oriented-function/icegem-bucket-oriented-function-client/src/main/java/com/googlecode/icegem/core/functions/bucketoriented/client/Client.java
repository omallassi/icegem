package com.googlecode.icegem.core.functions.bucketoriented.client;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.googlecode.icegem.core.functions.bucketoriented.common.functions.BucketOrientedFunction;
import com.googlecode.icegem.core.functions.bucketoriented.common.utils.ConsoleUtils;

import java.util.*;

/**
 * A simple client that connects to a server and reads all data from bucket(s) specified via filter key(s).
 *
 * @author Andrey Stepanov aka standy
 */
public class Client {
    /** Region data  */
    private static Region<Object, Object> data;
    /** Field cache  */
    private static ClientCache cache;

    /**
     * Client entry point.
     *
     * @param args of type String[]
     * @throws Exception when
     */
    public static void main(String[] args) throws Exception {
        createCacheAndRegion();

        ConsoleUtils.waitForEnter("Press 'Enter' to populate region by data");

        populateRegion(data);

        ConsoleUtils.waitForEnter("Data population has been finished. Press 'Enter' to start function execution");

        // For not existed key GemFire will execute this function at least on one member.
        executeFunction(Arrays.asList(0));

        // For existed key GemFire send this function to a member
        // that stores primary or redundant copy of this bucket.
        executeFunction(Arrays.asList(1));

        executeFunction(Arrays.asList(2));

        executeFunction(Arrays.asList(1,2));

        ConsoleUtils.waitForEnter("Press 'Enter' to stop the client");

        cache.close();
    }

    /**
     * Creates cache and region based on cache xml.
     */
    public static void createCacheAndRegion() {
        cache = new ClientCacheFactory()
                .set("cache-xml-file", "client-cache.xml")
                .create();
        System.out.println("Client has been started");
        data = cache.getRegion("data");
    }

    /**
     * Populates region by test data.
     *
     * @param region of type Region<Object, Object>
     */
    public static void populateRegion(Region<Object, Object> region) {
        for (int i = 1; i <= 500; i++) {
            region.put(i, Integer.toString(i));
        }
    }

    /**
     * Executes function that will be send only to those members that contains specified bucket(s).
     *
     * @param filterKeys of type List<Integer>
     */
    @SuppressWarnings({"unchecked"})
    private static void executeFunction(List<Integer> filterKeys)  {
        System.out.println("Execute function with filter key(s) : " + filterKeys);

        BucketOrientedFunction function = new BucketOrientedFunction();
        FunctionService.registerFunction(function);

        // If we want to execute a function on a set of buckets,
        // we can specify a set of keys that will represent each such bucket (one key for each bucket).
        Set<Integer> filter = new HashSet<Integer>();
        filter.addAll(filterKeys);

        List<BucketOrientedFunction.Result> results = (List<BucketOrientedFunction.Result>) FunctionService.onRegion(data)
                // Now we will execute our function only on those bucket(s) that we specify via key(s).
                .withFilter(filter)
                .execute(function)
                .getResult();

        Map<Integer, Set<Object>> allResults = new HashMap<Integer, Set<Object>>();
        for (BucketOrientedFunction.Result result: results) {
            allResults.putAll(result.getResults());
        }

        printResults(allResults);
    }

    /**
     * Prints results of function.
     *
     * @param results of type Map<Integer, Set<Object>>
     */
    private static void printResults(Map<Integer, Set<Object>> results) {
        int totalNumberOfEntries = 0;
        for (int bucketId : results.keySet()) {
            System.out.println("Bucket with id = " + bucketId + " contains entries with id: " + results.get(bucketId));
            totalNumberOfEntries += results.get(bucketId).size();
        }
        System.out.println("Total number of entries in buckets that satisfy filter keys: " + totalNumberOfEntries);
    }
}