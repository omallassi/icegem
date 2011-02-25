package com.griddynamics.gemfire.serialization.perf.impl;

import com.gemstone.gemfire.DataSerializer;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author igolovach
 */
public class GemFireDeserializationRunnable implements ExceptionalRunnable {
    private final DataOutput out = new NullDataOutput();
    private final DataSerializer ser;
    private final byte[] data;

    public GemFireDeserializationRunnable(final Object bean) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
        this.ser = GemFireSerializationRunnable.MAP.get(bean.getClass());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ser.toData(bean, new DataOutputStream(baos));
        } catch (IOException e) {
            throw new RuntimeException("Never here!", e);
        }
        data = baos.toByteArray();
    }

    public void run() throws Throwable {
        ser.fromData(new DataInputStream(new ByteArrayInputStream(data)));
    }

    public int size() {
        return data.length;
    }
}
