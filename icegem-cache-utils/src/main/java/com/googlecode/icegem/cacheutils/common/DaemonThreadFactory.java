package com.googlecode.icegem.cacheutils.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory  implements ThreadFactory {
    private final ThreadFactory factory = Executors.defaultThreadFactory();

    public Thread newThread(Runnable r) {
        Thread t = factory.newThread(r);
        t.setDaemon(true);
        return t;
    }
}
