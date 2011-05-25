package com.googlecode.icegem.query.pagination;

import com.gemstone.gemfire.cache.GemFireCache;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.LimitExceededException;
import java.util.*;

/**
 * This component allows to execute paginated queries both from client and peer/server sides.
 * It caches paginated query results in a help region and allows to iterate on them using paginated query API.
 *
 * Query results will be ordered by entry keys automatically, if a key object implements java.lang.Comparable interface.
 * @see Comparable
 *
 * See http://code.google.com/p/icegem/wiki/Documentation#Paginated_Query for more details.
 *
 * RESTRICTIONS:
 * 1). A query string for paginated query can be arbitrarily complex
 * but must return keys of entries that obtained during the query.
 * 2). For partitioned regions a query string must meet the requirements
 * described in a GemFire? documentation for querying partitioned regions.
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
    /** region for querying  */
    private Region<Object, V> queryRegion;
    /** help region for storing information about paginated queries  */
    private Region<PaginatedQueryPageKey, List<Object>> paginatedQueryInfoRegion;
    /** Field logger  */
    private Logger logger = LoggerFactory.getLogger(PaginatedQuery.class);

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString) throws RegionNotFoundException {
        this(cache, DEFAULT_QUERY_LIMIT, regionName, queryString, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param queryLimit limit on query result
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, int queryLimit, String regionName, String queryString)
            throws RegionNotFoundException {
        this(cache, queryLimit, regionName, queryString, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, int pageSize)
            throws RegionNotFoundException {
        this(cache, DEFAULT_QUERY_LIMIT, regionName, queryString, new Object[]{}, pageSize);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param queryLimit limit on query result
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, int queryLimit, String regionName, String queryString, int pageSize)
            throws RegionNotFoundException {
        this(cache, queryLimit, regionName, queryString, new Object[]{}, pageSize);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, Object[] queryParameters)
            throws RegionNotFoundException {
        this(cache, DEFAULT_QUERY_LIMIT, regionName, queryString, queryParameters, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param queryLimit limit on query result
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, int queryLimit, String regionName, String queryString,
                          Object[] queryParameters) throws RegionNotFoundException {
        this(cache, queryLimit, regionName, queryString, queryParameters, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, Object[] queryParameters,
                          int pageSize) throws RegionNotFoundException {
        this(cache, DEFAULT_QUERY_LIMIT, regionName, queryString, queryParameters, pageSize);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server (Cache) or client cache (ClientCache)
     * @param queryLimit limit on query result
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, int queryLimit, String regionName, String queryString,
                          Object[] queryParameters, int pageSize) throws RegionNotFoundException {
        queryService = cache.getQueryService();

        queryRegion = cache.getRegion(regionName);
        if (queryRegion == null) {
            RegionNotFoundException e = new RegionNotFoundException("Region for querying [" +
                    regionName + "] has not been found");
            logger.warn(e.getMessage());
            throw e;
        }

        paginatedQueryInfoRegion = cache.getRegion(PAGINATED_QUERY_INFO_REGION_NAME);
        if (paginatedQueryInfoRegion == null) {
            RegionNotFoundException e =  new RegionNotFoundException("Help region [" +
                    PAGINATED_QUERY_INFO_REGION_NAME + "] for storing " +
                    "information about paginated queries has not been found");
            logger.warn(e.getMessage());
            throw e;
        }

        if (pageSize < 1) {
            IllegalArgumentException e = new IllegalArgumentException("Page size must be positive");
            logger.warn(e.getMessage());
            throw e;
        }

        if (queryLimit < 1) {
            IllegalArgumentException e = new IllegalArgumentException("Query limit must be positive");
            logger.warn(e.getMessage());
            throw e;
        }
        this.queryLimit = queryLimit;

        pageKey = new PaginatedQueryPageKey(queryString, queryParameters, pageSize);
    }

    /**
     * Returns entries for a specified page number.
     * Use getTotalNumberOfPages() method to know how many pages this query has.
     *
     * @param pageNumber number of page to return
     * @return List<V> list of entries
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public List<V> page(int pageNumber) throws FunctionDomainException, TypeMismatchException,
            QueryInvocationTargetException, NameResolutionException, LimitExceededException {
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
        return getSortedValues(entriesKeysForPage);
    }

    /**
     * Return the next to the current page.
     * For the first call of this method it will be the first page.
     * Use hasNext() method to check that the query has the next page.
     *
     * @return List<V> list of entries
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public List<V> next() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException,
            NameResolutionException, LimitExceededException {
        return page(++currentPageNumber);
    }

    /**
     * Returns the previous to the current page.
     * Use hasPrevious() method to check that the query has a previous page.
     *
     * @return List<Object> list of entries
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public List<V> previous() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException,
            NameResolutionException, LimitExceededException {
        return page(--currentPageNumber);
    }

    /**
     * Checks that query has the next page.
     *
     * @return boolean
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public boolean hasNext() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException,
            NameResolutionException, LimitExceededException {
        return pageNumberExists(currentPageNumber + 1);
    }

    /**
     * Checks that query has the previous page.
     *
     * @return boolean
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public boolean hasPrevious() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException,
            NameResolutionException, LimitExceededException {
        return pageNumberExists(currentPageNumber - 1);
    }

    /**
     * Returns a total number of query entries.
     *
     * @return total number of entries
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public int getTotalNumberOfEntries() throws FunctionDomainException, TypeMismatchException,
            QueryInvocationTargetException, NameResolutionException, LimitExceededException {
        storePaginatedQueryInfoIfNeeded();
        return totalNumberOfEntries;
    }

    /**
     * Returns a total number of query pages.
     *
     * @return total number of pages
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public int getTotalNumberOfPages() throws FunctionDomainException, TypeMismatchException,
            QueryInvocationTargetException, NameResolutionException, LimitExceededException {
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
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     */
    public int getPageSize() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException,
            NameResolutionException {
        return pageKey.getPageSize();
    }

    /**
     * Checks that a specified page number exists.
     *
     * @param pageNumber of type int
     * @return boolean
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    public boolean pageNumberExists(int pageNumber) throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException, LimitExceededException {
        return pageNumber == 1 || !(pageNumber < 1 || pageNumber > getTotalNumberOfPages());
    }

    /**
     * Returns sorted by keys values for given keys.
     * Values will be sorted by keys, if keys implement Comparable interface.
     *
     * @param entriesKeysForPage of type List<Object>
     * @return List<V>
     */
    @SuppressWarnings({ "unchecked" })
    private List<V> getSortedValues(List<Object> entriesKeysForPage) {
        if (entriesKeysForPage.size() == 0) {
            return new ArrayList<V>(0);
        }
        Map<Object, V> entries = queryRegion.getAll(entriesKeysForPage);
        List<Object> keys = new ArrayList<Object>(entries.keySet());

        if (!(keys.get(0) instanceof Comparable)) {
            return new ArrayList<V> (entries.values());
        }

        Collections.sort(keys, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) o1).compareTo(o2);
            }
        });
        List<V> values = new ArrayList<V>(entries.size());
        for (Object key : keys) {
            values.add(entries.get(key));
        }
        return values;
    }

    /**
     * Stores paginated query info if it has not been stored yet.
     *
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     * @throws javax.naming.LimitExceededException when query limit will be exceeded
     */
    @SuppressWarnings({ "unchecked" })
    private void storePaginatedQueryInfoIfNeeded() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException, LimitExceededException {
        pageKey.setPageNumber(PAGE_NUMBER_FOR_GENERAL_INFO);
        List<Object> paginatedQueryGeneralInfo = paginatedQueryInfoRegion.get(pageKey);

        if (paginatedQueryGeneralInfo == null) {
            Query query = queryService.newQuery(addQueryLimit(pageKey.getQueryString()));
            SelectResults<Object> results = (SelectResults<Object>) query.execute(pageKey.getQueryParameters());
            if (results.size() > queryLimit) {
                LimitExceededException e = new LimitExceededException("Size of query results has exceeded limit ("
                        + queryLimit + " entries). You should refine the query.");
                logger.warn(e.getMessage());
                throw e;
            }
            storePaginatedQueryPagesAndGeneralInfo(results.asList());
        }
    }

    /**
     * Stores paginated query pages and general info.
     *
     * @param keys of type List<Object>
     */
    @SuppressWarnings({ "unchecked" })
    private void storePaginatedQueryPagesAndGeneralInfo(List<Object> keys) {
        storePaginatedQueryGeneralInfo(keys.size());

        if (keys.size() > 0 && keys.get(0) instanceof Comparable) {
            Collections.sort(keys, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) o1).compareTo(o2);
                }
            });
        }

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
        paginatedQueryInfoRegion.put(pageKey, Arrays.asList((Object) totalNumberOfEntries));
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
}
