package itest.com.googlecode.icegem.query.pagination;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.*;
import com.gemstone.gemfire.cache.query.*;
import com.googlecode.icegem.query.pagination.PaginatedQuery;
import com.googlecode.icegem.query.pagination.PaginatedQueryPageKey;
import com.googlecode.icegem.utils.RegionUtils;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;
import itest.com.googlecode.icegem.query.common.model.Person;
import itest.com.googlecode.icegem.query.common.utils.PersonUtils;
import org.fest.assertions.Assertions;
import org.testng.annotations.*;

import javax.naming.LimitExceededException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Tests for paginated query.
 *
 * @author Andrey Stepanov aka standy
 */
public class PaginatedQueryTest {
    /** Field LOCATOR_PORT  */
    private static final int LOCATOR_PORT = 10355;
    /** Field cache  */
    private static ClientCache cache;
    /** Region for querying  */
    private static Region<Object, Object> data;
    /** Help region for storing information about paginated queries  */
    private static Region<PaginatedQueryPageKey, List<Object>> paginatedQueryInfo;
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

    @BeforeMethod
    public void after() throws InterruptedException, IOException {
        RegionUtils.clearPartitionedRegion(data);
        RegionUtils.clearPartitionedRegion(paginatedQueryInfo);
    }

    @Test
    public void testCreation() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException,
            NameResolutionException {
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getPageSize()).as("Paginated query has not been created").isEqualTo(PaginatedQuery.DEFAULT_PAGE_SIZE);
    }

    @Test(expectedExceptions = RegionNotFoundException.class)
    public void testCreationForNotExistingQueryRegion() throws FunctionDomainException, QueryInvocationTargetException,
            TypeMismatchException, NameResolutionException {
        new PaginatedQuery(cache, "not_existing", "SELECT * FROM /data.keySet");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreationWithWrongPageSize() throws FunctionDomainException, QueryInvocationTargetException,
            TypeMismatchException, NameResolutionException {
        new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet", 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreationWithWrongQueryLimit() throws FunctionDomainException, QueryInvocationTargetException,
            TypeMismatchException, NameResolutionException {
        int queryLimit = -1;
        new PaginatedQuery(cache, queryLimit, "data", "SELECT * FROM /data.keySet");
    }

    @Test
    public void testGetPageSize() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException,
            NameResolutionException {
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getPageSize()).as("Default page size was not correct").isEqualTo(PaginatedQuery.DEFAULT_PAGE_SIZE);
        query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet", 10);
        Assertions.assertThat(query.getPageSize()).as("Custom page size has not been set").isEqualTo(10);
    }

    @Test
    public void testGetTotalNumberOfEntries() throws QueryException {
        String queryString = "SELECT * FROM /data.keySet";
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{},
                PaginatedQuery.DEFAULT_PAGE_SIZE, PaginatedQuery.PAGE_NUMBER_FOR_GENERAL_INFO);
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString);
        int totalNumberOfEntries = query.getTotalNumberOfEntries();
        List<Object> pageKeys = paginatedQueryInfo.get(pageKey);
        Assertions.assertThat(pageKeys != null).as("Total number of entries has not been stored").isTrue();
        assert pageKeys != null;
        Assertions.assertThat(pageKeys.get(0)).as("Total number of entries was not correct").isEqualTo(0);
        Assertions.assertThat(totalNumberOfEntries).as("Total number of entries was not correct").isEqualTo(0);

        RegionUtils.clearPartitionedRegion(paginatedQueryInfo);

        PersonUtils.populateRegionByPersons(data, 10);
        query = new PaginatedQuery(cache, "data", queryString);
        totalNumberOfEntries = query.getTotalNumberOfEntries();
        pageKeys = paginatedQueryInfo.get(pageKey);
        Assertions.assertThat(pageKeys != null).as("Total number of entries has not been stored").isTrue();
        assert pageKeys != null;
        Assertions.assertThat(pageKeys.get(0)).as("Total number of entries was not correct").isEqualTo(10);
        Assertions.assertThat(totalNumberOfEntries).as("Total number of entries was not correct").isEqualTo(10);
    }

    @Test
    public void testGetTotalNumberOfPages() throws QueryException {
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Total number of pages was not correct").isEqualTo(1);

        PersonUtils.populateRegionByPersons(data, 100);
        query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet", 10);
        Assertions.assertThat(query.getTotalNumberOfPages())
                .as("Total number of pages was not correct").isEqualTo(100 / 10);

        RegionUtils.clearPartitionedRegion(data);
        RegionUtils.clearPartitionedRegion(paginatedQueryInfo);

        PersonUtils.populateRegionByPersons(data, 101);
        query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getTotalNumberOfPages()).
                as("Total number of pages was not correct").isEqualTo(100 / PaginatedQuery.DEFAULT_PAGE_SIZE + 1);
    }

    @Test
    public void testPageMethodForEmptyResults() throws QueryException {
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 20;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, "data", queryString, pageSize);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, pageSize);

        int pageNumber = 1;
        pageKey.setPageNumber(pageNumber);

        List<Person> pageEntries = query.page(pageNumber);

        List<Object> pageKeys = paginatedQueryInfo.get(pageKey);
        Assertions.assertThat(pageKeys != null).as("Paginated query has not been stored").isTrue();
        assert pageKeys != null;
        Assertions.assertThat(pageKeys.size()).as("Total number of keys for the first page was not correct").isEqualTo(0);
        Assertions.assertThat(pageEntries.size()).as("Number of paginated entries for the first page was not correct").isEqualTo(0);
    }

    @Test
    public void testPageMethodForNotFullPage() throws QueryException {
        int numberOfEntriesForPopulation = 10;
        PersonUtils.populateRegionByPersons(data, numberOfEntriesForPopulation);
        String queryString = "SELECT DISTINCT d.key, d.value.socialNumber FROM /data.entrySet d ORDER BY d.value.socialNumber";
        int pageSize = 20;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, "data", queryString, pageSize);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, pageSize);

        int pageNumber = 1;
        pageKey.setPageNumber(pageNumber);

        List<Person> pageEntries = query.page(pageNumber);

        List<Object> pageKeys = paginatedQueryInfo.get(pageKey);
        Assertions.assertThat(pageKeys != null).as("Paginated query has not been stored").isTrue();
        assert pageKeys != null;
        Assertions.assertThat(pageKeys.size()).as("Total number of keys for the first page was not correct").isEqualTo(numberOfEntriesForPopulation);
        Assertions.assertThat(pageKeys.contains(1)).as("The first page does not contain key = 1").isTrue();
        Assertions.assertThat(pageKeys.contains(10)).as("The first page does not contain key = 10").isTrue();
        Assertions.assertThat(pageKeys.get(0)).as("The first page contains key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(pageKeys.get(9)).as("The first page contains key = 10 on the wrong position").isEqualTo(10);

        Assertions.assertThat(pageEntries.size()).as("Number of paginated entries for the first page was not correct").isEqualTo(numberOfEntriesForPopulation);
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 1)).as("The first page does not contain entry with key = 1").isTrue();
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 10)).as("The first page does not contain entry with key = 10").isTrue();
        Assertions.assertThat(pageEntries.get(0).getSocialNumber()).as("The first page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(pageEntries.get(9).getSocialNumber()).as("The first page contains entry with key = 10 on the wrong position").isEqualTo(10);
    }

    @Test
    public void testPageMethod() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        String queryString = "SELECT DISTINCT d.key, d.value.socialNumber FROM /data.entrySet d ORDER BY d.value.socialNumber";
        int pageSize = 20;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, "data", queryString, pageSize);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, pageSize);

        int pageNumber = 1;
        pageKey.setPageNumber(pageNumber);

        List<Person> pageEntries = query.page(pageNumber);

        List<Object> pageKeys = paginatedQueryInfo.get(pageKey);
        Assertions.assertThat(pageKeys != null).as("Paginated query has not been stored").isTrue();
        assert pageKeys != null;
        Assertions.assertThat(pageKeys.size()).as("Total number of keys for the first page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(pageKeys.contains(1)).as("The first page does not contain key = 1").isTrue();
        Assertions.assertThat(pageKeys.contains(20)).as("The first page does not contain key = 20").isTrue();
        Assertions.assertThat(pageKeys.get(0)).as("The first page contains key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(pageKeys.get(19)).as("The first page contains key = 20 on the wrong position").isEqualTo(20);

        Assertions.assertThat(pageEntries.size()).as("Number of paginated entries for the first page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 1)).as("The first page does not contain entry with key = 1").isTrue();
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 20)).as("The first page does not contain entry with key = 20").isTrue();
        Assertions.assertThat(pageEntries.get(0).getSocialNumber()).as("The first page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(pageEntries.get(19).getSocialNumber()).as("The first page contains entry with key = 20 on the wrong position").isEqualTo(20);

        pageNumber = 5;
        pageKey.setPageNumber(pageNumber);
        pageEntries = query.page(pageNumber);

        pageKeys = paginatedQueryInfo.get(pageKey);
        Assertions.assertThat(pageKeys != null).as("Paginated query has not been stored").isTrue();
        assert pageKeys != null;
        Assertions.assertThat(pageKeys.size()).as("Total number of keys for the fifth page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(pageKeys.contains(81)).as("The fifth page does not contain key = 81").isTrue();
        Assertions.assertThat(pageKeys.contains(100)).as("The fifth page does not contain key = 100").isTrue();
        Assertions.assertThat(pageKeys.get(0)).as("The fifth page contains key = 81 on the wrong position").isEqualTo(81);
        Assertions.assertThat(pageKeys.get(19)).as("The fifth page contains key = 100 on the wrong position").isEqualTo(100);

        Assertions.assertThat(pageEntries.size()).as("Number of paginated entries for the fifth page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 81)).as("The fifth page does not contain entry with key = 81").isTrue();
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 100)).as("The fifth page does not contain entry with key = 100").isTrue();
        Assertions.assertThat(pageEntries.get(0).getSocialNumber()).as("The fifth page contains entry with key = 81 on the wrong position").isEqualTo(81);
        Assertions.assertThat(pageEntries.get(19).getSocialNumber()).as("The fifth page contains entry with key = 100 on the wrong position").isEqualTo(100);
    }

    @Test
    public void testNextMethod() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        int pageSize = 20;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, "data",
                "SELECT DISTINCT d.key, d.value.socialNumber FROM /data.entrySet d ORDER BY d.value.socialNumber", pageSize);

        int pageNumber = 1;

        List<Person> pageEntries = query.page(pageNumber);
        List<Person> pageEntriesUsingNext = query.next();

        Assertions.assertThat(pageEntriesUsingNext.size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(pageEntriesUsingNext.get(0).getSocialNumber()).as("The next page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(pageEntriesUsingNext.get(19).getSocialNumber()).as("The next page contains entry with key = 20 on the wrong position").isEqualTo(20);
        Assertions.assertThat(pageEntriesUsingNext).as("Query results from page and next methods were not the same").isEqualTo(pageEntries);

        pageNumber = 2;
        pageEntries = query.page(pageNumber);
        pageEntriesUsingNext = query.next();

        Assertions.assertThat(pageEntriesUsingNext.size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(pageEntriesUsingNext.get(0).getSocialNumber()).as("The next page contains entry with key = 21 on the wrong position").isEqualTo(21);
        Assertions.assertThat(pageEntriesUsingNext.get(19).getSocialNumber()).as("The next page contains entry with key = 40 on the wrong position").isEqualTo(40);
        Assertions.assertThat(pageEntriesUsingNext).as("Query results from page and next methods were not the same").isEqualTo(pageEntries);
    }

    @Test
    public void testHasNextMethod() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 20);
        int pageSize = 10;
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet", pageSize);

        Assertions.assertThat(query.hasNext()).as("hasNext method has been worked incorrectly").isTrue();
        Assertions.assertThat(query.next().size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);

        Assertions.assertThat(query.hasNext()).as("hasNext method has been worked incorrectly").isTrue();
        Assertions.assertThat(query.next().size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);

        Assertions.assertThat(query.hasNext()).as("hasNext method has been worked incorrectly").isFalse();
    }

    @Test
    public void testPreviousMethod() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        int pageSize = 20;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, "data",
                "SELECT DISTINCT d.key, d.value.socialNumber FROM /data.entrySet d ORDER BY d.value.socialNumber", pageSize);

        query.next();
        query.next();
        query.next();

        int pageNumber = 2;
        List<Person> pageEntries = query.page(pageNumber);

        List<Person> pageEntriesUsingPrevious = query.previous();

        Assertions.assertThat(pageEntriesUsingPrevious.size()).as("Number of paginated entries for the previous page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(pageEntriesUsingPrevious.get(0).getSocialNumber()).as("The previous page contains entry with key = 21 on the wrong position").isEqualTo(21);
        Assertions.assertThat(pageEntriesUsingPrevious.get(19).getSocialNumber()).as("The previous page contains entry with key = 40 on the wrong position").isEqualTo(40);
        Assertions.assertThat(pageEntriesUsingPrevious).as("Query results from page and previous methods were not the same").isEqualTo(pageEntries);

        pageNumber = 1;
        pageEntries = query.page(pageNumber);
        pageEntriesUsingPrevious = query.previous();

        Assertions.assertThat(pageEntriesUsingPrevious.size()).as("Number of paginated entries for the previous page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(pageEntriesUsingPrevious.get(0).getSocialNumber()).as("The previous page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(pageEntriesUsingPrevious.get(19).getSocialNumber()).as("The previous page contains entry with key = 20 on the wrong position").isEqualTo(20);
        Assertions.assertThat(pageEntriesUsingPrevious).as("Query results from page and previous methods were not the same").isEqualTo(pageEntries);
    }

    @Test
    public void testHasPreviousMethod() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 20);
        int pageSize = 10;
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet", pageSize);

        query.next();
        query.next();

        Assertions.assertThat(query.hasPrevious()).as("hasPrevious method has been worked incorrectly").isTrue();
        Assertions.assertThat(query.previous().size()).as("Number of paginated entries for the previous page was not correct").isEqualTo(pageSize);

        Assertions.assertThat(query.hasPrevious()).as("hasPrevious method has been worked incorrectly").isFalse();
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testGetNotExistedPage() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 10);
        String queryString = "SELECT * FROM /data.keySet";
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, PaginatedQuery.DEFAULT_PAGE_SIZE);

        int pageNumber = 2;
        pageKey.setPageNumber(pageNumber);

        query.page(pageNumber);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testQueryOrderByWithoutKeyInProjection() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        PaginatedQuery query = new PaginatedQuery(cache, "data",
                "SELECT DISTINCT e.value.socialNumber, e.value.socialNumber FROM /data.entrySet e ORDER BY e.value.socialNumber");
        query.page(1);
    }

    @Test(expectedExceptions = ServerOperationException.class)
    public void testQueryOrderByWithoutOrderByFieldInProjection() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        PaginatedQuery query = new PaginatedQuery(cache, "data",
                "SELECT DISTINCT e.key FROM /data.entrySet e ORDER BY e.value.socialNumber");
        query.page(1);
    }

    @Test
    public void testPaginatedComplexQuering() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        PaginatedQuery query = new PaginatedQuery(cache, "data",
                "SELECT DISTINCT e.key, e.value.socialNumber FROM /data.entrySet e WHERE e.value.socialNumber = $1 ORDER BY e.value.socialNumber", new Object[]{1});

        List pageEntries = query.page(1);

        Assertions.assertThat(pageEntries.size()).as("Number of paginated entries for the first page was not correct").isEqualTo(1);
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 1)).as("The first page does not contain entry with key = 1").isTrue();
    }

    @Test
    public void testQueryLimit() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        int queryLimit = 50;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, queryLimit, "data", "SELECT * FROM /data.keySet");

        List<Person> results = query.next();
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Wrong number of paginated query pages").isEqualTo(3);
        Assertions.assertThat(query.getTotalNumberOfEntries()).as("Iimit was not used").isEqualTo(queryLimit);
        Assertions.assertThat(query.isLimitExceeded()).as("Limit flag was not set").isEqualTo(true);
    }

    @Test
    public void testInitialQueryLimitThatLowerThanPaginatedQueryLimit() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        int queryLimit = 50;
        int initialLimit = 10;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, queryLimit, "data", "SELECT * FROM /data.keySet limit " + initialLimit);
        List<Person> results = query.next();
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Wrong number of paginated query pages").isEqualTo(1);
        Assertions.assertThat(results.size()).as("Initial limit was not used").isEqualTo(initialLimit);
        Assertions.assertThat(query.isLimitExceeded()).as("Limit flag was set").isEqualTo(false);

        query = new PaginatedQuery<Person>(cache, queryLimit, "data", "SELECT * FROM /data.keySet LIMIT " + initialLimit);
        results = query.next();
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Wrong number of paginated query pages").isEqualTo(1);
        Assertions.assertThat(results.size()).as("Initial limit was not used").isEqualTo(initialLimit);
        Assertions.assertThat(query.isLimitExceeded()).as("Limit flag was set").isEqualTo(false);
    }

    @Test
    public void testInitialQueryLimitThatHigherThanPaginatedQueryLimit() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        int queryLimit = 60;
        int initialLimit = 70;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, queryLimit, "data", "SELECT * FROM /data.keySet limit " + initialLimit);
        List<Person> results = query.next();
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Wrong number of paginated query pages").isEqualTo(3);
        Assertions.assertThat(query.getTotalNumberOfEntries()).as("Initial limit was not used").isEqualTo(queryLimit);
        Assertions.assertThat(query.isLimitExceeded()).as("Limit flag was not set").isEqualTo(true);
    }

    @Test
    public void testInitialQueryLimitThatEqualToPaginatedQueryLimit() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 100);
        int queryLimit = 10;
        int initialLimit = 10;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, queryLimit, "data", "SELECT * FROM /data.keySet limit " + initialLimit);
        List<Person> results = query.next();
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Wrong number of paginated query pages").isEqualTo(1);
        Assertions.assertThat(results.size()).as("Initial limit was not used").isEqualTo(initialLimit);
    }

    @Test
    public void testInitialQueryLimitThatEqualToPaginatedQueryLimitForSmallSizeOfEntries() throws QueryException {
        PersonUtils.populateRegionByPersons(data, 5);
        int queryLimit = 6;
        int initialLimit = 10;
        PaginatedQuery<Person> query = new PaginatedQuery<Person>(cache, queryLimit, "data", "SELECT * FROM /data.keySet limit " + initialLimit);
        List<Person> results = query.next();
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Wrong number of paginated query pages").isEqualTo(1);
        Assertions.assertThat(results.size()).as("Initial limit was not used").isEqualTo(5);
    }

    /**
     * Starts a client.
     */
    private void startClient() {
        ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolLocator("localhost", LOCATOR_PORT);
        cache = clientCacheFactory.set("log-level", "warning").create();
        ClientRegionFactory<Object, Object> regionFactory =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        data = regionFactory.create("data");
        ClientRegionFactory<PaginatedQueryPageKey, List<Object>> regionFactoryForHelpRegion =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        paginatedQueryInfo = regionFactoryForHelpRegion.create(PaginatedQuery.PAGINATED_QUERY_INFO_REGION_NAME);
    }

    /**
     * Starts two cache servers for tests.
     *
     * @throws IOException when
     * @throws InterruptedException when
     */
    private void startCacheServers() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=paginatedQueryServerProperties.properties"}, null);
        cacheServer2 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=paginatedQueryServerProperties.properties"}, null);
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
