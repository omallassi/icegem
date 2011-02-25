package com.griddynamics.gemfire.serialization;

import com.gemstone.gemfire.DataSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Parent for tests with common utility code
 *
 * @author igolovach
 */
public class TestParent {

    public static final String MSG = "You test throw Exception. We mask it by RuntimeException. If you need concrete type - write serialization logic yourself.";

    public <T> T registerSerializeAndDeserialize(T obj) {
        try {
            // register
            HierarchyRegistry.registerAll(getContextClassLoader(), obj.getClass());
            // serialize
            final ByteArrayOutputStream buff = new ByteArrayOutputStream();
            DataSerializer.writeObject(obj, new DataOutputStream(buff));
            byte[] data = buff.toByteArray();

            // Deserialize
            return (T) DataSerializer.readObject(new DataInputStream(new ByteArrayInputStream(data)));
        } catch (Exception e) {
            throw new RuntimeException(MSG, e);
        }
    }

    public <T> T serializeAndDeserialize(T obj) {
        try {
            // serialize
            final ByteArrayOutputStream buff = new ByteArrayOutputStream();
            DataSerializer.writeObject(obj, new DataOutputStream(buff), false);
            byte[] data = buff.toByteArray();

            // Deserialize
            return (T) DataSerializer.readObject(new DataInputStream(new ByteArrayInputStream(data)));
        } catch (Exception e) {
            throw new RuntimeException(MSG, e);
        }
    }

    public ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
