package itest.com.googlecode.icegem.query.pagination;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.*;
import com.gemstone.gemfire.cache.query.*;
import com.googlecode.icegem.query.pagination.PaginatedQuery;
import com.googlecode.icegem.query.pagination.PaginatedQueryPageKey;
import com.googlecode.icegem.utils.CacheUtils;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import itest.com.googlecode.icegem.query.common.domain.Person;
import itest.com.googlecode.icegem.query.common.utils.PersonUtils;
import org.fest.assertions.Assertions;
import org.testng.annotations.*;

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
    public void tearDown() throws IOException {
        cache.close();
        stopCacheServers();
    }

    @AfterMethod
    public void after() {
        CacheUtils.clearPartitionedRegion(data);
        CacheUtils.clearPartitionedRegion(paginatedQueryInfo);
    }

    @Test
    public void testCreation() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getPageSize()).as("Paginated query has not been created").isEqualTo(PaginatedQuery.DEFAULT_PAGE_SIZE);
    }

    @Test(expectedExceptions = RegionNotFoundException.class)
    public void testCreationForNotExistingQueryRegion() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        new PaginatedQuery(cache, "not_existing", "SELECT * FROM /data.keySet");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreationWithWrongPageSize() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet", 0);
    }

    @Test
    public void testGetPageSize() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getPageSize()).as("Default page size was not correct").isEqualTo(PaginatedQuery.DEFAULT_PAGE_SIZE);
        query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet", 10);
        Assertions.assertThat(query.getPageSize()).as("Custom page size has not been set").isEqualTo(10);
    }

    @Test
    public void testGetTotalNumberOfEntries() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
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

        CacheUtils.clearPartitionedRegion(paginatedQueryInfo);

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
    public void testGetTotalNumberOfPages() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        PaginatedQuery query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getTotalNumberOfPages()).as("Total number of pages was not correct").isEqualTo(1);

        CacheUtils.clearPartitionedRegion(data);
        CacheUtils.clearPartitionedRegion(paginatedQueryInfo);

        PersonUtils.populateRegionByPersons(data, 100);
        query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getTotalNumberOfPages())
                .as("Total number of pages was not correct").isEqualTo(100 / PaginatedQuery.DEFAULT_PAGE_SIZE);

        CacheUtils.clearPartitionedRegion(data);
        CacheUtils.clearPartitionedRegion(paginatedQueryInfo);

        PersonUtils.populateRegionByPersons(data, 101);
        query = new PaginatedQuery(cache, "data", "SELECT * FROM /data.keySet");
        Assertions.assertThat(query.getTotalNumberOfPages()).
                as("Total number of pages was not correct").isEqualTo(100 / PaginatedQuery.DEFAULT_PAGE_SIZE + 1);
    }

    @Test
    public void testPageMethodForEmptyResults() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 20;
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, pageSize);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, pageSize);

        int pageNumber = 1;
        pageKey.setPageNumber(pageNumber);

        List<Object> pageEntries = query.page(pageNumber);

        List<Object> pageKeys = paginatedQueryInfo.get(pageKey);
        Assertions.assertThat(pageKeys != null).as("Paginated query has not been stored").isTrue();
        assert pageKeys != null;
        Assertions.assertThat(pageKeys.size()).as("Total number of keys for the first page was not correct").isEqualTo(0);
        Assertions.assertThat(pageEntries.size()).as("Number of paginated entries for the first page was not correct").isEqualTo(0);
    }

    @Test
    public void testPageMethodForNotFullPage() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        int numberOfEntriesForPopulation = 10;
        PersonUtils.populateRegionByPersons(data, numberOfEntriesForPopulation);
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 20;
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, pageSize);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, pageSize);

        int pageNumber = 1;
        pageKey.setPageNumber(pageNumber);

        List<Object> pageEntries = query.page(pageNumber);

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
        Assertions.assertThat(((Person) pageEntries.get(0)).getSocialNumber()).as("The first page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(((Person) pageEntries.get(9)).getSocialNumber()).as("The first page contains entry with key = 10 on the wrong position").isEqualTo(10);
    }

    @Test
    public void testPageMethod() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        PersonUtils.populateRegionByPersons(data, 100);
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 20;
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, pageSize);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, pageSize);

        int pageNumber = 1;
        pageKey.setPageNumber(pageNumber);

        List<Object> pageEntries = query.page(pageNumber);

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
        Assertions.assertThat(((Person) pageEntries.get(0)).getSocialNumber()).as("The first page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(((Person) pageEntries.get(19)).getSocialNumber()).as("The first page contains entry with key = 20 on the wrong position").isEqualTo(20);

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
        Assertions.assertThat(((Person) pageEntries.get(0)).getSocialNumber()).as("The fifth page contains entry with key = 81 on the wrong position").isEqualTo(81);
        Assertions.assertThat(((Person) pageEntries.get(19)).getSocialNumber()).as("The fifth page contains entry with key = 100 on the wrong position").isEqualTo(100);
    }

    @Test
    public void testNextMethod() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        PersonUtils.populateRegionByPersons(data, 100);
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 20;
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, pageSize);

        int pageNumber = 1;

        List<Object> pageEntries = query.page(pageNumber);
        List<Object> pageEntriesUsingNext = query.next();

        Assertions.assertThat(pageEntriesUsingNext.size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(((Person) pageEntriesUsingNext.get(0)).getSocialNumber()).as("The next page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(((Person) pageEntriesUsingNext.get(19)).getSocialNumber()).as("The next page contains entry with key = 20 on the wrong position").isEqualTo(20);
        Assertions.assertThat(pageEntriesUsingNext).as("Query results from page and next methods were not the same").isEqualTo(pageEntries);

        pageNumber = 2;
        pageEntries = query.page(pageNumber);
        pageEntriesUsingNext = query.next();

        Assertions.assertThat(pageEntriesUsingNext.size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(((Person) pageEntriesUsingNext.get(0)).getSocialNumber()).as("The next page contains entry with key = 21 on the wrong position").isEqualTo(21);
        Assertions.assertThat(((Person) pageEntriesUsingNext.get(19)).getSocialNumber()).as("The next page contains entry with key = 40 on the wrong position").isEqualTo(40);
        Assertions.assertThat(pageEntriesUsingNext).as("Query results from page and next methods were not the same").isEqualTo(pageEntries);
    }

    @Test
    public void testHasNextMethod() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        PersonUtils.populateRegionByPersons(data, 20);
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 10;
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, pageSize);

        Assertions.assertThat(query.hasNext()).as("hasNext method has been worked incorrectly").isTrue();
        Assertions.assertThat(query.next().size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);

        Assertions.assertThat(query.hasNext()).as("hasNext method has been worked incorrectly").isTrue();
        Assertions.assertThat(query.next().size()).as("Number of paginated entries for the next page was not correct").isEqualTo(pageSize);

        Assertions.assertThat(query.hasNext()).as("hasNext method has been worked incorrectly").isFalse();
    }

    @Test
    public void testPreviousMethod() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        PersonUtils.populateRegionByPersons(data, 100);
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 20;
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, pageSize);

        query.next();
        query.next();
        query.next();

        int pageNumber = 2;
        List<Object> pageEntries = query.page(pageNumber);

        List<Object> pageEntriesUsingPrevious = query.previous();

        Assertions.assertThat(pageEntriesUsingPrevious.size()).as("Number of paginated entries for the previous page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(((Person) pageEntriesUsingPrevious.get(0)).getSocialNumber()).as("The previous page contains entry with key = 21 on the wrong position").isEqualTo(21);
        Assertions.assertThat(((Person) pageEntriesUsingPrevious.get(19)).getSocialNumber()).as("The previous page contains entry with key = 40 on the wrong position").isEqualTo(40);
        Assertions.assertThat(pageEntriesUsingPrevious).as("Query results from page and previous methods were not the same").isEqualTo(pageEntries);

        pageNumber = 1;
        pageEntries = query.page(pageNumber);
        pageEntriesUsingPrevious = query.previous();

        Assertions.assertThat(pageEntriesUsingPrevious.size()).as("Number of paginated entries for the previous page was not correct").isEqualTo(pageSize);
        Assertions.assertThat(((Person) pageEntriesUsingPrevious.get(0)).getSocialNumber()).as("The previous page contains entry with key = 1 on the wrong position").isEqualTo(1);
        Assertions.assertThat(((Person) pageEntriesUsingPrevious.get(19)).getSocialNumber()).as("The previous page contains entry with key = 20 on the wrong position").isEqualTo(20);
        Assertions.assertThat(pageEntriesUsingPrevious).as("Query results from page and previous methods were not the same").isEqualTo(pageEntries);
    }

    @Test
    public void testHasPreviousMethod() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        PersonUtils.populateRegionByPersons(data, 20);
        String queryString = "SELECT * FROM /data.keySet";
        int pageSize = 10;
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, pageSize);

        query.next();
        query.next();

        Assertions.assertThat(query.hasPrevious()).as("hasPrevious method has been worked incorrectly").isTrue();
        Assertions.assertThat(query.previous().size()).as("Number of paginated entries for the previous page was not correct").isEqualTo(pageSize);

        Assertions.assertThat(query.hasPrevious()).as("hasPrevious method has been worked incorrectly").isFalse();
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testGetNotExistedPage() throws NameResolutionException, FunctionDomainException, QueryInvocationTargetException, TypeMismatchException {
        PersonUtils.populateRegionByPersons(data, 10);
        String queryString = "SELECT * FROM /data.keySet";
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString);
        PaginatedQueryPageKey pageKey = new PaginatedQueryPageKey(queryString, new Object[]{}, PaginatedQuery.DEFAULT_PAGE_SIZE);

        int pageNumber = 2;
        pageKey.setPageNumber(pageNumber);

        query.page(pageNumber);
    }
    
    @Test
    public void testPaginatedComplexQuering() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        PersonUtils.populateRegionByPersons(data, 100);
        String queryString = "SELECT e.key FROM /data.entrySet e WHERE e.value.socialNumber = $1";
        PaginatedQuery query = new PaginatedQuery(cache, "data", queryString, new Object[]{1});

        List<Object> pageEntries = query.page(1);

        Assertions.assertThat(pageEntries.size()).as("Number of paginated entries for the first page was not correct").isEqualTo(1);
        Assertions.assertThat(PersonUtils.containsPersonWithSocialNumber(pageEntries, 1)).as("The first page does not contain entry with key = 1").isTrue();
    }

    /**
     * Starts a client.
     *
     * Important:
     * If you plan to establish a client connection with only one cache server,
     * use direct connection to this server via client's pool.
     * If you plan to establish a client connection with more than one cache server,
     * use client connection to this servers via specified in client's pool locator.
     */
    @SuppressWarnings({ "unchecked" })
    private void startClient() {
        ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolLocator("localhost", LOCATOR_PORT);
//        ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolServer("SERVER_HOST", "SERVER_PORT");
        cache = clientCacheFactory.set("log-level", "warning").create();
        ClientRegionFactory regionFactory =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        data = regionFactory.create("data");
        paginatedQueryInfo = regionFactory.create(PaginatedQuery.PAGINATED_QUERY_INFO_REGION_NAME);
    }

    /**
     * Starts two cache servers for tests.
     *
     * @throws IOException when
     * @throws InterruptedException when
     */
    private void startCacheServers() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(Server.class, new String[]{CacheUtils.START_EMBEDDED_LOCATOR_COMMAND});
        cacheServer2 = javaProcessLauncher.runWithConfirmation(Server.class);
    }

    /**
     * Stops cache servers.
     *
     * @throws IOException when
     */
    private void stopCacheServers() throws IOException {
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
    }
}
