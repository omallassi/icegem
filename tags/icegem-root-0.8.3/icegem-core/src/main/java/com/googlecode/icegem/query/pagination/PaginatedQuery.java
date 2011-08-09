package com.googlecode.icegem.query.pagination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionService;
import com.gemstone.gemfire.cache.query.FunctionDomainException;
import com.gemstone.gemfire.cache.query.NameResolutionException;
import com.gemstone.gemfire.cache.query.Query;
import com.gemstone.gemfire.cache.query.QueryException;
import com.gemstone.gemfire.cache.query.QueryInvocationTargetException;
import com.gemstone.gemfire.cache.query.QueryService;
import com.gemstone.gemfire.cache.query.RegionNotFoundException;
import com.gemstone.gemfire.cache.query.SelectResults;
import com.gemstone.gemfire.cache.query.Struct;
import com.gemstone.gemfire.cache.query.TypeMismatchException;

/**
 * This component allows to execute paginated queries both from client and peer/server sides.
 * It caches paginated query results in a help region and allows to iterate on them using paginated query API.
 *
 * See http://code.google.com/p/icegem/wiki/Documentation#Paginated_Query for more details and examples.
 *
 * RESTRICTIONS:
 * 1). A query string for paginated query can be arbitrarily complex
 * but entry key must be part of projection list.
 * 2). For partitioned regions a query string must meet the requirements
 * described in a GemFire documentation for querying partitioned regions.
 *
 * ORDER BY on Partitioned Regions
 * A paginated query supports order by functionality on partitioned regions.
 * The fields specified in the order by clause must be part of the projection list.
 *
 * Limiting of results:
 * Paginated query result can be limited. By default this limit is 1000 entries.
 * You can specify a custom limit value via paginated query constructors argument.
 * If query results exceeds this limit:
 *  - only a specified limit number of entries will be cached and returned;
 *  - flag 'limitExceeded' will be set to 'true'.
 *
 * Examples:
 *
 * SELECT * FROM /data.keySet;
 * SELECT DISTINCT d.key, d.value.field1 FROM /data.entrySet d ORDER BY d.value.field1;
 *
 * @author Andrey Stepanov aka standy
 */
public class PaginatedQuery<V> {
    /** page size by default  */
    public static final int DEFAULT_PAGE_SIZE = 20;
    /** Default limit on query result  */
    public static final int DEFAULT_QUERY_LIMIT = 1000;
    /** number of page that will be store general information about paginated query (e.g. total number of query entries) */
    public static final int PAGE_NUMBER_FOR_GENERAL_INFO = -1;
    /** name of a help region for storing information about paginated queries  */
    public static final String PAGINATED_QUERY_INFO_REGION_NAME = "paginated_query_info";
    /** common key for query pages */
    private PaginatedQueryPageKey pageKey;
    /** Field totalNumberOfEntries  */
    private int totalNumberOfEntries;
    /** Field currentPageNumber  */
    private int currentPageNumber = 0;
    /** Field queryService  */
    private QueryService queryService;
    /** limit on query result */
    private int queryLimit;
    /** flag that indicates that limit has been exceeded */
    private boolean limitExceeded;
    /** region for querying  */
    private Region<Object, V> queryRegion;
    /** help region for storing information about paginated queries  */
    private Region<PaginatedQueryPageKey, List<Object>> paginatedQueryInfoRegion;
    /** Field logger  */
    private Logger logger = LoggerFactory.getLogger(PaginatedQuery.class);

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, Region<Object, V> region, String queryString) throws RegionNotFoundException {
        this(queryService, DEFAULT_QUERY_LIMIT, region, queryString, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param queryLimit limit on query result
     * @param region The region for querying.
     * @param queryString query string that must return entry keys
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, int queryLimit, Region<Object, V> region, String queryString)
            throws RegionNotFoundException {
        this(queryService, queryLimit, region, queryString, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param region The region for querying.
     * @param queryString query string that must return entry keys
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, Region<Object, V> queryRegion, String queryString, int pageSize)
            throws RegionNotFoundException {
        this(queryService, DEFAULT_QUERY_LIMIT, queryRegion, queryString, new Object[]{}, pageSize);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param queryLimit limit on query result
     * @param region The region for querying.
     * @param queryString query string that must return entry keys
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, int queryLimit, Region<Object, V> region, String queryString, int pageSize)
            throws RegionNotFoundException {
        this(queryService, queryLimit, region, queryString, new Object[]{}, pageSize);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param region The region for querying.
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, Region<Object, V> region, String queryString, Object[] queryParameters)
            throws RegionNotFoundException {
        this(queryService, DEFAULT_QUERY_LIMIT, region, queryString, queryParameters, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param queryLimit limit on query result
     * @param region The region for querying.
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, int queryLimit, Region<Object, V> region, String queryString,
                          Object[] queryParameters) throws RegionNotFoundException {
        this(queryService, queryLimit, region, queryString, queryParameters, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param region The region for querying.
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, Region<Object, V> region, String queryString, Object[] queryParameters,
                          int pageSize) throws RegionNotFoundException {
        this(queryService, DEFAULT_QUERY_LIMIT, region, queryString, queryParameters, pageSize);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param queryService The service to run the query.
     * @param queryLimit limit on query result
     * @param region The region for querying.
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(QueryService queryService, int queryLimit, Region<Object, V> queriedRegion, String queryString,
                          Object[] queryParameters, int pageSize) throws RegionNotFoundException {
        this.queryService = queryService;

        this.queryRegion = queriedRegion;
        if (queryRegion == null) {
        	throw new NullPointerException("Query region have to be provided");
        }

        RegionService regionService = queryRegion.getRegionService();
        paginatedQueryInfoRegion = regionService.getRegion(PAGINATED_QUERY_INFO_REGION_NAME);
        
        if (paginatedQueryInfoRegion == null) {
            RegionNotFoundException e =  new RegionNotFoundException("Help region [" +
                    PAGINATED_QUERY_INFO_REGION_NAME + "] for storing " +
                    "information about paginated queries has not been found");
            logger.warn(e.getMessage());
            throw e;
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be positive");
        }

        if (queryLimit < 1) {
            throw new IllegalArgumentException("Query limit must be positive");
        }
        this.queryLimit = queryLimit;

        pageKey = new PaginatedQueryPageKey(queryString, queryParameters, pageSize);
    }

    /**
     * Returns entries for a specified page number.
     * Use getTotalNumberOfPages() method to know how many pages this query has.
     *
     * @param pageNumber number of page to return
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     *
     * @return List<V> list of entries
     */
    public List<V> page(int pageNumber) throws QueryException {
        storePaginatedQueryInfoIfNeeded();
        if (!pageNumberExists(pageNumber)) {
            IndexOutOfBoundsException e =  new IndexOutOfBoundsException("A page number {" + pageNumber + "} " +
                    "was out of bounds: [1, " + getTotalNumberOfPages() + "]");
            logger.warn(e.getMessage());
            throw e;
        }
        pageKey.setPageNumber(pageNumber);
        List<Object> entriesKeysForPage = paginatedQueryInfoRegion.get(pageKey);

        if (entriesKeysForPage == null) {
            IllegalStateException e = new IllegalStateException("There is no query results for page " + pageNumber);
            logger.warn(e.getMessage());
            throw e;
        }
        return getValues(entriesKeysForPage);
    }

    /**
     * Return the next to the current page.
     * For the first call of this method it will be the first page.
     * Use hasNext() method to check that the query has the next page.
     *
     * @return List<V> list of entries
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public List<V> next() throws QueryException {
        return page(++currentPageNumber);
    }

    /**
     * Returns the previous to the current page.
     * Use hasPrevious() method to check that the query has a previous page.
     *
     * @return List<Object> list of entries
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public List<V> previous() throws QueryException {
        return page(--currentPageNumber);
    }

    /**
     * Checks that query has the next page.
     *
     * @return boolean
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public boolean hasNext() throws QueryException {
        return pageNumberExists(currentPageNumber + 1);
    }

    /**
     * Checks that query has the previous page.
     *
     * @return boolean
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public boolean hasPrevious() throws QueryException {
        return pageNumberExists(currentPageNumber - 1);
    }

    /**
     * Returns a total number of query entries.
     *
     * @return total number of entries
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public int getTotalNumberOfEntries() throws QueryException {
        storePaginatedQueryInfoIfNeeded();
        return totalNumberOfEntries;
    }

    /**
     * Returns a total number of query pages.
     *
     * @return total number of pages
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public int getTotalNumberOfPages() throws QueryException {
        storePaginatedQueryInfoIfNeeded();
        if (isEmpty()) {
            return 1;
        }
        int total = totalNumberOfEntries / pageKey.getPageSize();
        if (totalNumberOfEntries % pageKey.getPageSize() > 0) {
            total += 1;
        }
        return total;
    }

    /**
     * Returns size of page.
     *
     * @return page size
     */
    public int getPageSize() {
        return pageKey.getPageSize();
    }

    /**
     * Checks that a specified page number exists.
     *
     * @param pageNumber of type int
     * @return boolean
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public boolean pageNumberExists(int pageNumber) throws QueryException {
        return pageNumber == 1 || !(pageNumber < 1 || pageNumber > getTotalNumberOfPages());
    }

    /**
     * Gets value of a flag that indicates excess of query limit.
     *
     * @return boolean
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    public boolean isLimitExceeded() throws QueryException {
        storePaginatedQueryInfoIfNeeded();
        return limitExceeded;
    }

    /**
     * Returns values for given keys.
     *
     * @param entriesKeysForPage of type List<Object>
     * @return List<V>
     */
    private List<V> getValues(List<Object> entriesKeysForPage) {
        if (entriesKeysForPage.size() == 0) {
            return new ArrayList<V>(0);
        }
        Map<Object, V> entriesMap = queryRegion.getAll(entriesKeysForPage);

        List<V> entries = new ArrayList<V>(entriesKeysForPage.size());
        for (Object key : entriesKeysForPage) {
            entries.add(entriesMap.get(key));
        }
        return entries;
    }

    /**
     * Stores paginated query info if it has not been stored yet.
     *
     * @throws com.gemstone.gemfire.cache.query.QueryException during query execution
     */
    @SuppressWarnings({ "unchecked" })
    private void storePaginatedQueryInfoIfNeeded() throws QueryException {
        pageKey.setPageNumber(PAGE_NUMBER_FOR_GENERAL_INFO);
        List<Object> paginatedQueryGeneralInfo = paginatedQueryInfoRegion.get(pageKey);

        if (paginatedQueryGeneralInfo == null) {
            Query query = queryService.newQuery(addQueryLimit(pageKey.getQueryString()));
            SelectResults<Object> results = null;
            try {
                results = (SelectResults<Object>) query.execute(pageKey.getQueryParameters());
            } catch (FunctionDomainException e) {
                handleException(e);
            } catch (TypeMismatchException e) {
                handleException(e);
            } catch (NameResolutionException e) {
                handleException(e);
            } catch (QueryInvocationTargetException e) {
                handleException(e);
            }
            if (results.size() > queryLimit) {
                limitExceeded = true;
                String warningMessage = "Size of query results has exceeded limit ("
                        + queryLimit + " entries). Only " + queryLimit + " query results have been returned. " +
                        "Maybe you should refine the query.";
                logger.warn(warningMessage);
            } else {
                limitExceeded = false;
            }
            if (results.getCollectionType().getElementType().isStructType()) {
                // List of structures. Should extract keys from it.
                List<Object> keys = new ArrayList<Object>(results.size());

                for (Object result : results) {
                    Object key;
                    try {
                        key = ((Struct) result).get("key");
                    } catch (IllegalArgumentException e) {
                        IllegalArgumentException exception = new IllegalArgumentException(e.getMessage() +
                                " (hint: maybe you forgot to include entry key into query projection list)");
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                    keys.add(key);
                }
                storePaginatedQueryPagesAndGeneralInfo(keys);
            } else {
                // List of keys.
                storePaginatedQueryPagesAndGeneralInfo(results.asList());
            }
        }
    }

    /**
     * Stores paginated query pages and general info.
     *
     * @param keys of type List<Object>
     */
    private void storePaginatedQueryPagesAndGeneralInfo(List<Object> keys) {
        if (limitExceeded) {
            keys.remove(keys.size() - 1);
        }
        storePaginatedQueryGeneralInfo(keys.size());

        int keyNumber = 0;
        int pageNumber = 0;
        List<Object> pageKeys = new ArrayList<Object>();
        for (Object key : keys) {
            if (keyNumber % pageKey.getPageSize() == 0 && keyNumber != 0) {
                pageKey.setPageNumber(++pageNumber);
                paginatedQueryInfoRegion.put(pageKey, pageKeys);
                pageKeys.clear();
            }
            pageKeys.add(key);
            keyNumber++;
        }

        if (pageKeys.size() > 0 || pageNumber == 0) {
            pageKey.setPageNumber(++pageNumber);
            paginatedQueryInfoRegion.put(pageKey, pageKeys);
        }
    }

    /**
     * Stores paginated query general info (total number of query entries).
     *
     * @param totalNumberOfEntries of type int
     */
    private void storePaginatedQueryGeneralInfo(int totalNumberOfEntries) {
        pageKey.setPageNumber(PAGE_NUMBER_FOR_GENERAL_INFO);
        paginatedQueryInfoRegion.put(pageKey, Arrays.asList((Object) totalNumberOfEntries, limitExceeded));
        this.totalNumberOfEntries = totalNumberOfEntries;
    }

    /**
     * Checks that query doesn't have results.
     *
     * @return the empty (type boolean) of this PaginatedQuery object.
     */
    private boolean isEmpty() {
        return totalNumberOfEntries == 0;
    }

    /**
     * Limits query results.
     *
     * @param queryString of type String
     * @return String
     */
    private String addQueryLimit(String queryString) {
        int limitIndex = queryString.lastIndexOf("limit");
        if (limitIndex == -1) {
            limitIndex = queryString.lastIndexOf("LIMIT");
        }
        if (limitIndex == -1) {
            return queryString + " LIMIT " + (queryLimit + 1);
        }
        int limitNumber = Integer.parseInt(queryString.substring(limitIndex + 5).trim());
        return (limitNumber > queryLimit) ?
                queryString.substring(0, limitIndex) + " LIMIT " + (queryLimit + 1) : queryString;
    }

    /**
     * Handles throwable exceptions during query execution and replaces them by checked exception.
     *
     * @param e of type Throwable
     * @throws com.gemstone.gemfire.cache.query.QueryException checked exception
     */
    private void handleException(Exception e) throws QueryException {
        throw new QueryException("Exception has been thrown during query execution. " +
                "Cause exception message: " + e.getMessage(), e);
    }
}