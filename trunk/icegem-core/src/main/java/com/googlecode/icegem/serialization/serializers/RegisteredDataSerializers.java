package com.googlecode.icegem.serialization.serializers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: akondratyev
 */
public class RegisteredDataSerializers {
    private static List<Class<?>> dataSerializers;

    public static List<Class<?>> getDataSerializers() {
        return dataSerializers;
    }

    static {
        dataSerializers = new ArrayList<Class<?>>();
        dataSerializers.add(UUIDDataSerializer.class);
        dataSerializers.add(JodaTimeDataSerializer.class);
        dataSerializers.add(TimestampDataSerializer.class);

        //todo: make normal class register
    }
}
