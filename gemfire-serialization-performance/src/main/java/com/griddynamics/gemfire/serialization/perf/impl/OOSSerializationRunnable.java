package com.griddynamics.gemfire.serialization.perf.impl;

import java.io.ObjectOutputStream;

/**
 * @author igolovach
 */
public class OOSSerializationRunnable implements ExceptionalRunnable {
    private final Object bean;

    public OOSSerializationRunnable(Object bean) {
        this.bean = bean;
    }

    public void run() throws Throwable {
        ObjectOutputStream oos = new ObjectOutputStream(new NullOutputStream());
        oos.writeUnshared(bean);
        oos.flush();
//                oos.close(); //todo: do we need this?
    }
}
