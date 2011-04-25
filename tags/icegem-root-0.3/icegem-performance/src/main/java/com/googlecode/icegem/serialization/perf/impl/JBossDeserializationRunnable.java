package com.googlecode.icegem.serialization.perf.impl;

import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author igolovach
 */
public class JBossDeserializationRunnable implements ExceptionalRunnable {
    private final byte[] data;

    public JBossDeserializationRunnable(Object bean) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JBossObjectOutputStream oos = new JBossObjectOutputStream(baos);
            oos.writeUnshared(bean);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Never here!", e);
        }
        data = baos.toByteArray();
    }

    public void run() throws Throwable {
        new JBossObjectInputStream(new ByteArrayInputStream(data)).readObject();
    }

    public int size() {
        return data.length;
    }
}
