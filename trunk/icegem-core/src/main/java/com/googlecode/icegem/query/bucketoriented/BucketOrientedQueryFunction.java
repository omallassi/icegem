package com.googlecode.icegem.query.bucketoriented;

import com.gemstone.gemfire.cache.execute.*;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.cache.query.*;
import com.gemstone.gemfire.cache.query.internal.DefaultQuery;
import com.gemstone.gemfire.internal.cache.LocalDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Function for executing query on a specified set of buckets.
 * Do not call this function directly. Use bucket oriented query service instead.
 *
 * @see com.googlecode.icegem.query.bucketoriented.BucketOrientedQueryService
 *
 * @author Andrey Stepanov aka standy
 */
public class BucketOrientedQueryFunction extends FunctionAdapter {
    /** Function ID. */
    private final static String FUNCTION_ID = BucketOrientedQueryFunction.class.getName();
    
    /** Logger. */
    private Logger logger = LoggerFactory.getLogger(BucketOrientedQueryFunction.class);

    /**
     * Executes specified via arguments query string.
     *
     * @param functionContext of type FunctionContext
     */
    @Override
    @SuppressWarnings({ "ThrowableInstanceNeverThrown", "unchecked" })
    public void execute(FunctionContext functionContext) {
        ResultSender<Serializable> resultSender = functionContext.getResultSender();
        
        RegionFunctionContext regionFunctionContext = (RegionFunctionContext) functionContext;

        if (functionContext.getArguments() == null) {
            handleException(new FunctionException("You must specify function argument for query execution."), resultSender);
            
            return;
        }
        
        if (!(functionContext.getArguments() instanceof BucketOrientedQueryFunctionArgument)) {
            handleException(new FunctionException("Function arguments must be of BucketOrientedQueryFunctionArgument.class"), resultSender);
            
            return;
        }

        BucketOrientedQueryFunctionArgument argument = (BucketOrientedQueryFunctionArgument) functionContext.getArguments();
        
        LocalDataSet localData = (LocalDataSet) PartitionRegionHelper.getLocalDataForContext(regionFunctionContext);
        
        QueryService queryService = localData.getCache().getQueryService();

        try {
            Query query = queryService.newQuery(argument.getQueryString());
            
            SelectResults result = (SelectResults) localData.executeQuery((DefaultQuery) query,
                    argument.getQueryParameters(), localData.getBucketSet());
            
            resultSender.lastResult((Serializable) formatResults(result));
        } catch (QueryInvalidException e) {
            handleException(e, resultSender);
        } catch (FunctionDomainException e) {
            handleException(e, resultSender);
        } catch (TypeMismatchException e) {
            handleException(e, resultSender);
        } catch (NameResolutionException e) {
            handleException(e, resultSender);
        } catch (QueryInvocationTargetException e) {
            handleException(e, resultSender);
        } catch (Throwable e) {
            handleException(e, resultSender);
        }
    }

    /**
     * Method getId returns the id of this BucketOrientedQueryFunction object.
     *
     * @return the id (type String) of this BucketOrientedQueryFunction object.
     */
    @Override
    public String getId() {
        return FUNCTION_ID;
    }

    /**
     * If you use redundancy for partitioned region then GemFire will send this
     * function to those members that contain primary or redundant copy of bucket(s).
     * It can increase number of members that will execute this function.
     * But if you want to send this function only to those members that store primary
     * copy of bucket, you must enable a function option "optimizeForWrite".
     *
     * See a forum link
     * http://forums.gemstone.com/viewtopic.php?f=3&t=496&hilit=bucket+Id&sid=f3b823b748bb253e5019e489c8480fbd
     * for details.
     *
     * @return boolean
     */
    @Override
    public boolean optimizeForWrite() {
        return false;
    }

    /**
     * Handles exceptions during query execution.
     *
     * @param e of type Throwable
     * @param resultSender of type ResultSender<Serializable>
     */
    @SuppressWarnings({ "ThrowableInstanceNeverThrown" })
    private void handleException(Throwable e, ResultSender<Serializable> resultSender) {
        e.printStackTrace();
        
        logger.warn(e.getMessage());
        
        resultSender.sendException(new FunctionException(e.getMessage()));
    }

    /**
     * Formats results of query execution.
     *
     * @param selectResults of type SelectResults
     * @return List<Object>
     */
    @SuppressWarnings({ "unchecked" })
    private List<Object> formatResults(SelectResults selectResults) {
        List<Object> results = new ArrayList<Object>(selectResults.size() + 1);
        
        results.addAll(selectResults.asList());
        results.add(selectResults.getCollectionType().getElementType());
        
        return results;
    }
}
