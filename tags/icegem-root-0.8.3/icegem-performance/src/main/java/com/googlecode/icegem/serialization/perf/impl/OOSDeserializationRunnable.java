package com.googlecode.icegem.serialization.perf.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author igolovach
 */
public class OOSDeserializationRunnable implements ExceptionalRunnable {
    private final byte[] data;

    public OOSDeserializationRunnable(Object bean) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeUnshared(bean);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Never here!", e);
        }
        data = baos.toByteArray();
    }

    public void run() throws Throwable {
        new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
    }

    public int size() {
        return data.length;
    }
}
