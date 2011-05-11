package com.googlecode.icegem.query.bucketoriented;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.query.QueryException;
import com.gemstone.gemfire.cache.query.SelectResults;
import com.gemstone.gemfire.cache.query.internal.ResultsCollectionWrapper;
import com.gemstone.gemfire.cache.query.types.ObjectType;

/**
 * Query service that allows to execute OQL queries on a specified set of buckets. This service can be used both on
 * client and server/peer sides. Note: this service works only on partition regions.
 * 
 * @author Andrey Stepanov aka standy
 */
public class BucketOrientedQueryService {
    /** Field logger */
    private static Logger logger = LoggerFactory.getLogger(BucketOrientedQueryService.class);

    /**
     * Executes a particular query on specified region using set of keys that represents buckets. The set of buckets is
     * determined by keys of entries that are stored in such buckets: - real and fake keys can be used (such key should
     * have the same routing object as bucket's keys have); - it will be enough to specify one key for each bucket. Work
     * of this method is based on execution of function.
     * 
     * @see QueryFunction
     * @param queryString OQL query string for execute
     * @param region partitioned region on which query will be executed
     * @param keys set of keys that specify buckets
     * @return SelectResults<Object>
     * @throws com.gemstone.gemfire.cache.query.QueryException when exception with execution occurs
     */
    @SuppressWarnings({ "unchecked" })
    public static SelectResults<Object> executeOnBuckets(String queryString, Region region, Set<Object> keys)
            throws QueryException {
        return executeOnBuckets(queryString, null, region, keys);
    }

    /**
     * Executes a particular query with parameters on specified region using a set of keys that represents buckets. The
     * set of buckets is determined by keys of entries that are stored in such buckets: - real and fake keys can be used
     * (such key should have the same routing object as bucket's keys have); - it will be enough to specify one key for
     * each bucket. Work of this method is based on execution of function.
     * 
     * @see QueryFunction
     * @param queryString OQL query string for execute
     * @param queryParameters of type Object[]
     * @param region partitioned region on which query will be executed
     * @param keys set of keys that specify buckets
     * @return SelectResults<Object>
     * @throws com.gemstone.gemfire.cache.query.QueryException when exception with execution occurs
     */
    @SuppressWarnings({ "unchecked" })
    public static SelectResults<Object> executeOnBuckets(String queryString, Object[] queryParameters,
                                                         Region region, Set<Object> keys) throws QueryException {
        if ((queryString == null) || (queryString.length() == 0)) {
            throw new QueryException("You must specify query string for execution");
        }

        int[] limitInfo = extractLimit(queryString);
        if (limitInfo[0] != -1) {
            queryString = queryString.substring(0, limitInfo[1]);
        }
        QueryFunctionArgument functionArgument = new QueryFunctionArgument(queryString, queryParameters);

        QueryFunction function = new QueryFunction();
        FunctionService.registerFunction(function);
        List<List<Object>> queryResults;
        try {
            queryResults = (List<List<Object>>) FunctionService.onRegion(region)
                    .withFilter(keys)
                    .withArgs(functionArgument)
                    .execute(function)
                    .getResult();
        } catch (FunctionException e) {
            logger.warn(e.getMessage());
            throw new QueryException(e.getMessage());
        }
        return formatSelectResults(queryResults, limitInfo[0]);
    }

    /**
     * Extracts limit and position of limit keyword from query string.
     * 
     * @param queryString of type String
     * @return int[]
     */
    private static int[] extractLimit(String queryString) {
        int limitIndex = queryString.lastIndexOf("limit");
        if (limitIndex == -1) {
            limitIndex = queryString.lastIndexOf("LIMIT");
        }
        if (limitIndex == -1) {
            return new int[] { -1, -1 };
        }
        String limitNumber = queryString.substring(limitIndex + 5);
        return new int[] { Integer.parseInt(limitNumber.trim()), limitIndex };
    }

    /**
     * Collects and formats query results into SelectResults. Limits query results based on limit value.
     * 
     * @param queryResults of type List<List<Object>>
     * @param limit of type int
     * @return SelectResults<Object>
     */
    @SuppressWarnings({ "unchecked" })
    private static SelectResults<Object> formatSelectResults(List<List<Object>> queryResults, int limit) {
        List<Object> list = new ArrayList<Object>();
        ObjectType baseElementType = null;
        for (List<Object> queryResult : queryResults) {
            ObjectType elementType = (ObjectType) queryResult.remove(queryResult.size() - 1);
            if (baseElementType == null) {
                baseElementType = elementType;
            } else if (!baseElementType.equals(elementType)) {
                throw new IllegalStateException("Collection types for query result are different");
            }
            list.addAll(queryResult);
        }
        return limit == -1 ? new ResultsCollectionWrapper(baseElementType, list) :
                new ResultsCollectionWrapper(baseElementType, list, limit);
    }
}
