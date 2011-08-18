package com.googlecode.icegem.serialization.perf.impl;

import org.jboss.serial.io.JBossObjectOutputStream;

/**
 * @author igolovach
 */
public class JBossSerializationRunnable implements ExceptionalRunnable {
    private final Object bean;

    public JBossSerializationRunnable(Object bean) {
        this.bean = bean;
    }

    public void run() throws Throwable {
        JBossObjectOutputStream oos = new JBossObjectOutputStream(new NullOutputStream());
        oos.writeUnshared(bean);
        oos.flush();
//                oos.close(); //todo: do we need this?
    }
}
