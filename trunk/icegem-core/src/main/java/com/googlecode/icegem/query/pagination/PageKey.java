package com.googlecode.icegem.query.pagination;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;

/**
 * Key for storing paginated query pages.
 *
 * @author Andrey Stepanov aka standy
 */
public class PageKey implements Serializable {
    /** Field queryString  */
    private String queryString;

	/** Field queryParameters  */
    private Object[] queryParameters;
    private int queryLimit;
    /** Field pageNumber  */
    private int pageNumber;
    private int pageSize;
	/**
     * Constructor PageKey creates a new PageKey instance.
     *
     * @param queryString the ODL query.
     * @param queryParameters the parameters.
     * @param pageSize the page size. 
     * @param pageNumber the number of page.
     */
    public PageKey(String queryString, Object[] queryParameters, int queryLimit, int pageSize, int pageNumber) {
        this.queryString = queryString;
        this.queryParameters = queryParameters;
        this.queryLimit = queryLimit;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageKey other = (PageKey) obj;
		if (pageNumber != other.pageNumber)
			return false;
		if (pageSize != other.pageSize)
			return false;
		if (queryLimit != other.queryLimit)
			return false;
		if (!Arrays.equals(queryParameters, other.queryParameters))
			return false;
		if (queryString == null) {
			if (other.queryString != null)
				return false;
		} else if (!queryString.equals(other.queryString))
			return false;
		return true;
	}

    public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		this.pageNumber = in.readInt();
		this.pageSize = in.readInt();
		this.queryLimit = in.readInt();
		this.queryString = DataSerializer.readString(in);
		this.queryParameters = DataSerializer.readObjectArray(in);
	}

	/**
     * Method getPageNumber returns the pageNumber of this PageKey object.
     *
     * @return the pageNumber (type int) of this PageKey object.
     */
    public int getPageNumber() {
        return pageNumber;
    }

	public int getPageSize() {
		return pageSize;
	}

	public int getQueryLimit() {
		return queryLimit;
	}

	/**
     * Method getQueryParameters returns the queryParameters of this PageKey object.
     *
     * @return the queryParameters (type Object[]) of this PageKey object.
     */
    public Object[] getQueryParameters() {
        return queryParameters;
    }

    /**
     * Method getQueryString returns the queryString of this PageKey object.
     *
     * @return the queryString (type String) of this PageKey object.
     */
    public String getQueryString() {
        return queryString;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pageNumber;
		result = prime * result + pageSize;
		result = prime * result + queryLimit;
		result = prime * result + Arrays.hashCode(queryParameters);
		result = prime * result
				+ ((queryString == null) ? 0 : queryString.hashCode());
		return result;
	}

    /**
     * Method setPageNumber sets the pageNumber of this PageKey object.
     *
     * @param pageNumber the pageNumber of this PageKey object.
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

    public void setQueryLimit(int queryLimit) {
		this.queryLimit = queryLimit;
	}


	/**
     * Method setQueryParameters sets the queryParameters of this PageKey object.
     *
     * @param queryParameters the queryParameters of this PageKey object.
     */
    public void setQueryParameters(Object[] queryParameters) {
        this.queryParameters = queryParameters;
    }

	/**
     * Method setQueryString sets the queryString of this PageKey object.
     *
     * @param queryString the queryString of this PageKey object.
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

	public void toData(DataOutput out) throws IOException {
		out.writeInt(this.pageNumber);
		out.writeInt(this.pageSize);
		out.writeInt(this.queryLimit);
		DataSerializer.writeString(this.queryString, out);
		DataSerializer.writeObjectArray(this.queryParameters, out);
	}

	@Override
	public String toString() {
		return "PageKey [queryString=" + queryString + ", queryParameters="
				+ Arrays.toString(queryParameters) + ", queryLimit="
				+ queryLimit + ", pageSize=" + pageSize + ", pageNumber="
				+ pageNumber + "]";
	}
}
