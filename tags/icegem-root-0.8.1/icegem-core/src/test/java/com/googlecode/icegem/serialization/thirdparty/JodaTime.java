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
    /*public boolean toData(Object obj, java.io.DataOutput out) throws java.io.IOException {
        // check arg is of correct type
        if (obj.getClass() != com.googlecode.icegem.serialization.thirdparty.JodaTime.class) {return false;}
        // increment thread-local method-frame counter
        com.googlecode.icegem.serialization.codegen.MethodFrameCounter.enterFrame();
        // convert to concrete type
        com.googlecode.icegem.serialization.thirdparty.JodaTime concrete = (com.googlecode.icegem.serialization.thirdparty.JodaTime) obj;

        out.writeInt(1);
        // this.dateTime -> byte[]
        if (concrete.getDateTime() == null) {
            out.writeByte(0);
        } else {
            if (concrete.getDateTime().getClass() != org.joda.time.DateTime) {
                String wrongClassName = concrete.getDateTime().getClass().getName();
                throw new com.gemstone.gemfire.ToDataException("Field of type org.joda.time.DateTime can contains value only of type org.joda.time.DateTime, not " + wrongClassName, null);
            }
            out.writeByte(1);
             com.gemstone.gemfire.DataSerializer.writeObject(concrete.getDateTime().toDateTime(), out);
        }

        // decrement thread-local method-frame counter
        com.googlecode.icegem.serialization.codegen.MethodFrameCounter.exitFrame();
        return true;
    }
*/
}
