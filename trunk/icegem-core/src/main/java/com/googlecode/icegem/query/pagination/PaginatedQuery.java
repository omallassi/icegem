package com.googlecode.icegem.query.pagination;

import com.gemstone.gemfire.cache.GemFireCache;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Query that support pagination.
 * This query can be used both on client and server sides.
 *
 * LIMITATIONS:
 * Query string that will be used for this paginated query must return entry keys.
 *
 * @author Andrey Stepanov aka standy
 */
public class PaginatedQuery {
    /** page size by default  */
    public static final int DEFAULT_PAGE_SIZE = 20;
    /** number of page that will be store general information about paginated query (e.g. total number of entries) */
    public static final int PAGE_NUMBER_FOR_GENERAL_INFO = -1;
    /** name of a help region for storing information about paginated queries  */
    public static final String PAGINATED_QUERY_INFO_REGION_NAME = "paginated_query_info";
    /** Field logger  */
    private Logger logger = LoggerFactory.getLogger(PaginatedQuery.class);
    /** common key for query pages */
    private PaginatedQueryPageKey pageKey;
    /** Field totalNumberOfEntries  */
    private int totalNumberOfEntries;
    /** Field currentPageNumber  */
    private int currentPageNumber = 0;
    /** Field queryService  */
    private QueryService queryService;
    /** region for querying  */
    private Region<Object, Object> queryRegion;
    /** help region for storing information about paginated queries  */
    private Region<PaginatedQueryPageKey, List<Object>> paginatedQueryInfoRegion;

    /**
     * Constructor PaginatedQuery creates a new PaginatedQuery instance.
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
     * Constructor PaginatedQuery creates a new PaginatedQuery instance.
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
     * Constructor PaginatedQuery creates a new PaginatedQuery instance.
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
     * Constructor PaginatedQuery creates a new PaginatedQuery instance.
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
     * Return entries for specified page number.
     * Use getTotalNumberOfPages() method to know how many pages this query has.
     *
     * @param pageNumber of type int
     * @return List<Object> list of entries
     * @throws FunctionDomainException when
     * @throws TypeMismatchException when
     * @throws QueryInvocationTargetException when
     * @throws NameResolutionException when
     */
    public List<Object> page(int pageNumber) throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        storePaginatedQueryInfoIfNeeded();
        if (!pageNumberExists(pageNumber)) {
            IndexOutOfBoundsException exception = new IndexOutOfBoundsException("A page number {" + pageNumber + "} " +
                    "was out of bounds: [1, " + getTotalNumberOfPages() + "]");
            logger.warn(exception.getMessage());
            throw exception;
        }
        pageKey.setPageNumber(pageNumber);
        List<Object> entriesKeysForPage = paginatedQueryInfoRegion.get(pageKey);

        return entriesKeysForPage != null ? getSortedValues(entriesKeysForPage) : new ArrayList<Object>(0);
    }

    /**
     * Return next to current page.
     * For the first call of this method it will be a first page.
     * Use hasNext() method to check that the query has next page.
     *
     * @return List<Object> list of entries
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     */
    public List<Object> next() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return page(++currentPageNumber);
    }

    /**
     * Return previous to current page.
     * Use hasPrevious() method to check that the query has previous page.
     *
     * @return List<Object> list of entries
     * @throws FunctionDomainException when
     * @throws QueryInvocationTargetException when
     * @throws TypeMismatchException when
     * @throws NameResolutionException when
     */
    public List<Object> previous() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return page(--currentPageNumber);
    }

    /**
     * Returns total number of query entries.
     *
     * @return the totalNumberOfEntries (type int) of this PaginatedQuery object.
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
     * @return the totalNumberOfPages (type int) of this PaginatedQuery object.
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
     * @return the pageSize (type int) of this PaginatedQuery object.
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
     * Returns sorted values for given keys.
     *
     * @param entriesKeysForPage of type List<Object>
     * @return List<Object>
     */
    @SuppressWarnings({ "unchecked" })
    private List<Object> getSortedValues(List<Object> entriesKeysForPage) {
        if (entriesKeysForPage.size() == 0) {
            return new ArrayList<Object>(0);
        }
        Map<Object, Object> entries = queryRegion.getAll(entriesKeysForPage);
        List<Object> keys = new ArrayList<Object>(entries.keySet());
        if (keys.get(0) instanceof Comparable) {
            Collections.sort(keys, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) o1).compareTo(o2);
                }
            });
        }
        List<Object> values = new ArrayList<Object>(entries.size());
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
     * Stores paginated query info if it was not stored.
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

    private void storePaginatedQueryGeneralInfo(int totalNumberOfEntries) {
        pageKey.setPageNumber(PAGE_NUMBER_FOR_GENERAL_INFO);
        paginatedQueryInfoRegion.put(pageKey, Arrays.asList((Object) totalNumberOfEntries));
        this.totalNumberOfEntries = totalNumberOfEntries;
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
     * Checks that region is empty.
     *
     * @return the empty (type boolean) of this PaginatedQuery object.
     */
    private boolean isEmpty() {
        return totalNumberOfEntries == 0;
    }
}
