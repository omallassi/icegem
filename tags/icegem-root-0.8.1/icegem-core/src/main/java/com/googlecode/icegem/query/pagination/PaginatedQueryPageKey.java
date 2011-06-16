package com.googlecode.icegem.query.pagination;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Key for storing paginated query pages.
 *
 * @author Andrey Stepanov aka standy
 */
public class PaginatedQueryPageKey implements Serializable {
    /** Field serialVersionUID  */
    private static final long serialVersionUID = -6604228935238313321L;
    /** Field queryString  */
    private String queryString;
    /** Field queryParameters  */
    private Object[] queryParameters;
    /** Field pageSize  */
    private int pageSize;
    /** Field pageNumber  */
    private int pageNumber;

    /**
     * Constructor PaginatedQueryPageKey creates a new PaginatedQueryPageKey instance.
     *
     * @param queryString of type String
     * @param queryParameters of type Object[]
     * @param pageSize of type int
     */
    public PaginatedQueryPageKey(String queryString, Object[] queryParameters, int pageSize) {
        this.queryString = queryString;
        this.queryParameters = queryParameters;
        this.pageSize = pageSize;
    }

    /**
     * Constructor PaginatedQueryPageKey creates a new PaginatedQueryPageKey instance.
     *
     * @param queryString of type String
     * @param queryParameters of type Object[]
     * @param pageSize of type int
     * @param pageNumber of type int
     */
    public PaginatedQueryPageKey(String queryString, Object[] queryParameters, int pageSize, int pageNumber) {
        this.queryString = queryString;
        this.queryParameters = queryParameters;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    /**
     * Method getPageNumber returns the pageNumber of this PaginatedQueryPageKey object.
     *
     * @return the pageNumber (type int) of this PaginatedQueryPageKey object.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Method setPageNumber sets the pageNumber of this PaginatedQueryPageKey object.
     *
     * @param pageNumber the pageNumber of this PaginatedQueryPageKey object.
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Method getQueryParameters returns the queryParameters of this PaginatedQueryPageKey object.
     *
     * @return the queryParameters (type Object[]) of this PaginatedQueryPageKey object.
     */
    public Object[] getQueryParameters() {
        return queryParameters;
    }

    /**
     * Method setQueryParameters sets the queryParameters of this PaginatedQueryPageKey object.
     *
     * @param queryParameters the queryParameters of this PaginatedQueryPageKey object.
     */
    public void setQueryParameters(Object[] queryParameters) {
        this.queryParameters = queryParameters;
    }

    /**
     * Method getQueryString returns the queryString of this PaginatedQueryPageKey object.
     *
     * @return the queryString (type String) of this PaginatedQueryPageKey object.
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Method setQueryString sets the queryString of this PaginatedQueryPageKey object.
     *
     * @param queryString the queryString of this PaginatedQueryPageKey object.
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Method getPageSize returns the pageSize of this PaginatedQueryPageKey object.
     *
     * @return the pageSize (type int) of this PaginatedQueryPageKey object.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Method setPageSize sets the pageSize of this PaginatedQueryPageKey object.
     *
     * @param pageSize the pageSize of this PaginatedQueryPageKey object.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Method equals.
     *
     * @param o of type Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaginatedQueryPageKey)) return false;

        PaginatedQueryPageKey pageKey = (PaginatedQueryPageKey) o;

        if (pageNumber != pageKey.pageNumber) return false;
        if (pageSize != pageKey.pageSize) return false;
        if (!Arrays.equals(queryParameters, pageKey.queryParameters)) return false;
        if (queryString != null ? !queryString.equals(pageKey.queryString) : pageKey.queryString != null)
            return false;

        return true;
    }

    /**
     * Method hashCode.
     * @return int
     */
    @Override
    public int hashCode() {
        int result = queryString != null ? queryString.hashCode() : 0;
        result = 31 * result + (queryParameters != null ? Arrays.hashCode(queryParameters) : 0);
        result = 31 * result + pageSize;
        result = 31 * result + pageNumber;
        return result;
    }
}
