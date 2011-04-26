package com.googlecode.icegem.serialization.serializers;

import com.gemstone.gemfire.DataSerializer;
import org.joda.time.DateTime;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * User: akondratyev
 */
public class JodaTimeDataSerializer extends DataSerializer {

    static {
        DataSerializer.register(JodaTimeDataSerializer.class);
    }

    public JodaTimeDataSerializer() {
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class<?>[] {DateTime.class};
    }

    @Override
    public boolean toData(Object o, DataOutput dataOutput) throws IOException {
        if (o instanceof DateTime) {
            DateTime dt = (DateTime) o;
            dataOutput.writeLong(dt.toDate().getTime());
            return true;
        }
        return false;
    }

    @Override
    public Object fromData(DataInput dataInput) throws IOException, ClassNotFoundException {
        long time = dataInput.readLong();
        return new DateTime(time);
    }

    @Override
    public int getId() {
        return 987234234;
    }
}
