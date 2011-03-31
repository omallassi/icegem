package com.griddynamics.icegem.serialization._jdktypes;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 344367690)
@BeanVersion(1)
public class DisallowedSqlDateBean {
    private java.sql.Date date;

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }
}
