package com.googlecode.icegem.query.bucketoriented;

import java.io.Serializable;

/**
 * Argument for query function that stores information about query string and query parameters.
 *
 * @author Andrey Stepanov aka standy.
 */
public class BucketOrientedQueryFunctionArgument implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = -2428574227320772082L;

    /** OQL query string. */
    private String queryString;

    /** Query parameters. */
    private Object[] queryParameters;

    /**
     * Creates new bucket oriented query function argument.
     *
     * @param queryString OQL query string.
     * @param queryParameters Query parameters.
     */
    public BucketOrientedQueryFunctionArgument(String queryString, Object[] queryParameters) {
	this.queryString = queryString;
	this.queryParameters = queryParameters;
    }

    /**
     * Gets query parameters.
     *
     * @return Query parameters.
     */
    public Object[] getQueryParameters() {
	return queryParameters;
    }

    /**
     * Query string.
     *
     * @return Query string.
     */
    public String getQueryString() {
	return queryString;
    }
}
