package com.googlecode.icegem.serialization.serializers;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.SerializationID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

/**
 * User: akondratyev
 */
public class UUIDDataSerializer extends DataSerializer implements SerializationID {

    static {
        DataSerializer.register(UUIDDataSerializer.class);
    }

    public UUIDDataSerializer() {
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class[] {UUID.class};
    }

    @Override
    public boolean toData(Object o, DataOutput dataOutput) throws IOException {
        if (o instanceof  UUID) {
            UUID uuid = (UUID) o;
            dataOutput.writeLong(uuid.getMostSignificantBits());
            dataOutput.writeLong(uuid.getLeastSignificantBits());
            return true;
        }
        return false;
    }

    @Override
    public Object fromData(DataInput dataInput) throws IOException, ClassNotFoundException {
        long mostSignificantBits = dataInput.readLong();
        long leastSignificantBits = dataInput.readLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    @Override
    public int getId() {
        return UUID_DATA_SERIALIZER_ID;
    }
}
