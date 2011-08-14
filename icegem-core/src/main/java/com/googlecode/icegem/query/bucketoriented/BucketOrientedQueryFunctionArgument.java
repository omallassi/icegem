package com.googlecode.icegem.query.bucketoriented;

import java.io.Serializable;

/**
 * Argument for query function that stores information
 * about query string and query parameters.
 *
 * @author Andrey Stepanov aka standy.
 */
public class BucketOrientedQueryFunctionArgument implements Serializable {
    /** Field serialVersionUID  */
    private static final long serialVersionUID = -2428574227320772082L;

    /** Field queryString  */
    private String queryString;

    /** Field queryParameters  */
    private Object[] queryParameters;

    /**
     * Constructor BucketOrientedQueryFunctionArgument creates a new BucketOrientedQueryFunctionArgument instance.
     *
     * @param queryString of type String
     * @param queryParameters of type Object[]
     */
    public BucketOrientedQueryFunctionArgument(String queryString, Object[] queryParameters) {
	this.queryString = queryString;
	this.queryParameters = queryParameters;
    }

    /**
     * Method getQueryParameters returns the queryParameters of this BucketOrientedQueryFunctionArgument object.
     *
     * @return the queryParameters (type Object[]) of this BucketOrientedQueryFunctionArgument object.
     */
    public Object[] getQueryParameters() {
	return queryParameters;
    }

    /**
     * Method getQueryString returns the queryString of this BucketOrientedQueryFunctionArgument object.
     *
     * @return the queryString (type String) of this BucketOrientedQueryFunctionArgument object.
     */
    public String getQueryString() {
	return queryString;
    }
}
