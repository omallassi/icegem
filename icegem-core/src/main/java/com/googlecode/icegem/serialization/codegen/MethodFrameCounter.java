package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.codegen.exception.IceGemRuntimeException;
import com.googlecode.icegem.serialization.codegen.exception.MethodFrameStackOverflowException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Analog of javassist.runtime.Cflow
 *
 * @author igolovach
 */

public class MethodFrameCounter extends ThreadLocal<AtomicInteger> {
    public final static int MAX_STACK_DEPTH = 256;
    public final static String STACK_OVERFLOW_MSG = "Too deep method frame stack - " + MAX_STACK_DEPTH + ". Perhaps cyclic references in serialized object have been found.";
    public final static String SYSTEM_PROPERTY_NAME = "icegem.serialization.control.methodframes";

    private static final ThreadLocal<AtomicInteger> local = new ThreadLocal<AtomicInteger>() {
        protected synchronized AtomicInteger initialValue() {
            return new AtomicInteger(0);
        }
    };

    /**
     * Increment the counter.
     */
    public static void enterFrame() {
        int counter = local.get().incrementAndGet();
        if (counter == MAX_STACK_DEPTH) {
            throw new MethodFrameStackOverflowException(STACK_OVERFLOW_MSG);
        }
    }

    /**
     * Decrement the counter.
     */
    public static void exitFrame() {
        int counter = local.get().decrementAndGet();
        if (counter < 0) {
            clearCounter();
            throw new IceGemRuntimeException("Method frame counter is less then 0. Some programming error: count(exitFrame) > count(enterFrame).");
        }
    }

    /**
     * Clear the counter.
     */
    private static void clearCounter() {
        local.get().set(0);
    }
}
