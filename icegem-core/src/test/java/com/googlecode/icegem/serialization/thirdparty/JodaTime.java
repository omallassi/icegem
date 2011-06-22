package com.googlecode.icegem.serialization.thirdparty;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import org.joda.time.DateTime;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 987345)
@BeanVersion(1)
public class JodaTime {
    private DateTime dateTime;

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
