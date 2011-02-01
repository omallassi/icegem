package com.griddynamics.gemfire.serialization._jdktypes;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * @author igolovach
 */

@SerializedClass(dataSerializerID = 344367690)
public class DisallowedSqlDateBean {
    private java.sql.Date date;

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }
}
