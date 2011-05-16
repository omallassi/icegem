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
 * TODO: javadocs will be added soon.
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

    public PaginatedQuery(GemFireCache cache, String regionName, String queryString) throws RegionNotFoundException {
        this(cache, regionName, queryString, DEFAULT_PAGE_SIZE);
    }

    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, int pageSize) throws RegionNotFoundException {
        this(cache, regionName, queryString, new Object[]{}, pageSize);
    }

    public PaginatedQuery(GemFireCache cache, String regionName, String queryString, Object[] queryParameters) throws RegionNotFoundException {
        this(cache, regionName, queryString, queryParameters, DEFAULT_PAGE_SIZE);
    }

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

    public List<Object> next() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return page(++currentPageNumber);
    }

    public List<Object> previous() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return page(--currentPageNumber);
    }

    public int getTotalNumberOfEntries() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        storePaginatedQueryInfoIfNeeded();
        return totalNumberOfEntries;
    }

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

    public int getPageSize() throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        return pageKey.getPageSize();
    }

    public boolean hasNext() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return pageNumberExists(currentPageNumber + 1);
    }

    public boolean hasPrevious() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        return pageNumberExists(currentPageNumber - 1);
    }

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

    private boolean pageNumberExists(int pageNumber) throws FunctionDomainException, TypeMismatchException, QueryInvocationTargetException, NameResolutionException {
        return !(pageNumber < 1 || pageNumber > getTotalNumberOfPages());
    }

    @SuppressWarnings({ "unchecked" })
    private void storePaginatedQueryInfoIfNeeded() throws FunctionDomainException, QueryInvocationTargetException, TypeMismatchException, NameResolutionException {
        pageKey.setPageNumber(PAGE_NUMBER_FOR_GENERAL_INFO);
        List<Object> paginatedQueryGeneralInfo = paginatedQueryInfoRegion.get(pageKey);

        if (paginatedQueryGeneralInfo == null) {
            Query query = queryService.newQuery(pageKey.getQueryString());
            SelectResults<Object> results = (SelectResults<Object>) query.execute(pageKey.getQueryParameters());
            storePaginatedQueryResultsInfo(results.asList());
        }
    }

    private void storePaginatedQueryGeneralInfo(int totalNumberOfEntries) {
        pageKey.setPageNumber(PAGE_NUMBER_FOR_GENERAL_INFO);
        paginatedQueryInfoRegion.put(pageKey, Arrays.asList((Object) totalNumberOfEntries));
        this.totalNumberOfEntries = totalNumberOfEntries;
    }

    @SuppressWarnings({ "unchecked" })
    private void storePaginatedQueryResultsInfo(List<Object> keys) {
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

    private boolean isEmpty() {
        return totalNumberOfEntries == 0;
    }
}
