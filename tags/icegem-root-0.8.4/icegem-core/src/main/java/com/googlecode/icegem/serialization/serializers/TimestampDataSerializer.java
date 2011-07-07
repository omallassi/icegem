package com.googlecode.icegem.serialization.serializers;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.SerializationID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * User: akondratyev
 */
public class TimestampDataSerializer extends DataSerializer implements SerializationID {

    static {
        DataSerializer.register(TimestampDataSerializer.class);
    }

    public TimestampDataSerializer() {
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class<?>[]{Timestamp.class};
    }

    @Override
    public boolean toData(Object o, DataOutput dataOutput) throws IOException {
        if (o instanceof  Timestamp) {
            Timestamp ts = (Timestamp) o;
            dataOutput.writeLong(ts.getTime());
            return true;
        }
        return false;
    }

    @Override
    public Object fromData(DataInput dataInput) throws IOException, ClassNotFoundException {
        long time = dataInput.readLong();
        return new Timestamp(time);
    }

    @Override
    public int getId() {
        return TIMESTAMP_DATA_SERIALIZER_ID;
    }
}
