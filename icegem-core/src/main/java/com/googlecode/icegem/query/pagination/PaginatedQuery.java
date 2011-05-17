package com.googlecode.icegem.query.pagination;

import com.gemstone.gemfire.cache.GemFireCache;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This component allows to execute paginated queries both from client and peer/server sides.
 * It caches paginated query results in a help region and allows to iterate on them using paginated query API.
 * 
 * Query results can be sorted by keys, if key object implements Comparable interface.
 * @see Comparable
 *
 * This query can be used both on client and server sides.
 *
 * LIMITATIONS:
 * Query string that will be used for this paginated query must return entry keys.
 *
 * @author Andrey Stepanov aka standy
 */
public class PaginatedQuery<V> {
    /** page size by default  */
    public static final int DEFAULT_PAGE_SIZE = 20;
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
    /** region for querying  */
    private Region<Object, V> queryRegion;
    /** help region for storing information about paginated queries  */
    private Region<PaginatedQueryPageKey, List<Object>> paginatedQueryInfoRegion;
    /** Field logger  */
    private Logger logger = LoggerFactory.getLogger(PaginatedQuery.class);

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server of client cache
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString) throws RegionNotFoundException {
        this(cache, regionName, queryString, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server of client cache
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, int pageSize) throws RegionNotFoundException {
        this(cache, regionName, queryString, new Object[]{}, pageSize);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server of client cache
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, Object[] queryParameters) throws RegionNotFoundException {
        this(cache, regionName, queryString, queryParameters, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PaginatedQuery instance.
     *
     * @param cache peer/server of client cache
     * @param regionName name of region for querying
     * @param queryString query string that must return entry keys
     * @param queryParameters parameters for query execution
     * @param pageSize size of page
     * @throws RegionNotFoundException when query region or help region were not founded
     */
    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, Object[] queryParameters, int pageSize) throws RegionNotFoundException {
        queryService = cache.getQueryService();

        queryRegion = cache.getRegion(regionName);
        if (queryRegion == null) {
            throw new RegionNotFoundException("Region for queries [" + PAGINATED_QUERY_INFO_REGION_NAME + "] has not been found");
        }

        paginatedQueryInfoRegion = cache.getRegion(PAGINATED_QUERY_INFO_REGION_NAME);
        if (paginatedQueryInfoRegion == null) {
            throw new RegionNotFoundException("Help region [" + PAGINATED_QUERY_INFO_REGION_NAME + "] for storing " +
                    "information about paginated queries has not been found");
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        pageKey = new PaginatedQueryPageKey(queryString, queryParameters, pageSize);
    }

    /**
     * Return entries for a specified page number.
     * Use getTotalNumberOfPages() method to know how many pages this query has.
     *
     * @param pageNumber number of page to return
     * @return List<V> list of entries
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     */
    public List<V> page(int pageNumber) throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        storePaginatedQueryInfoIfNeeded();
        if (!pageNumberExists(pageNumber)) {
            throw new IndexOutOfBoundsException("A page number {" + pageNumber + "} " +
                    "was out of bounds: [1, " + getTotalNumberOfPages() + "]");
        }
        pageKey.setPageNumber(pageNumber);
        List<Object> entriesKeysForPage = paginatedQueryInfoRegion.get(pageKey);

        return entriesKeysForPage != null ? getSortedValues(entriesKeysForPage) : new ArrayList<V>(0);
    }

    /**
     * Return next to the current page.
     * For the first call of this method it will be a first page.
     * Use hasNext() method to check that the query has a next page.
     *
     * @return List<V> list of entries
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     */
    public List<V> next() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return page(++currentPageNumber);
    }

    /**
     * Return previous to the current page.
     * Use hasPrevious() method to check that the query has a previous page.
     *
     * @return List<Object> list of entries
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     */
    public List<V> previous() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return page(--currentPageNumber);
    }

    /**
     * Returns a total number of query entries.
     *
     * @return total number of entries
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     */
    public int getTotalNumberOfEntries() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        storePaginatedQueryInfoIfNeeded();
        return totalNumberOfEntries;
    }

    /**
     * Returns total number of query pages.
     *
     * @return total number of pages
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     */
    public int getTotalNumberOfPages() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
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
    public int getPageSize() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        return pageKey.getPageSize();
    }

    /**
     * Checks that query has next page.
     *
     * @return boolean
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     */
    public boolean hasNext() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return pageNumberExists(currentPageNumber + 1);
    }

    /**
     * Checks that query has previous page.
     *
     * @return boolean
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     */
    public boolean hasPrevious() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return pageNumberExists(currentPageNumber - 1);
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
     * Checks that a specified page number exists.
     *
     * @param pageNumber of type int
     * @return boolean
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     */
    private boolean pageNumberExists(int pageNumber) throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        return !(pageNumber < 1 || pageNumber > getTotalNumberOfPages());
    }

    /**
     * Stores paginated query info if it has not been stored yet.
     *
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     */
    @SuppressWarnings({ "unchecked" })
    private void storePaginatedQueryInfoIfNeeded() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        pageKey.setPageNumber(PAGE_NUMBER_FOR_GENERAL_INFO);
        List<Object> paginatedQueryGeneralInfo = paginatedQueryInfoRegion.get(pageKey);

        if (paginatedQueryGeneralInfo == null) {
            Query query = queryService.newQuery(pageKey.getQueryString());
            SelectResults<Object> results = (SelectResults<Object>) query.execute(pageKey.getQueryParameters());
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
}
