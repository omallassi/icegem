package com.griddynamics.gemfire.serialization.codegen;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Analog of javassist.runtime.Cflow
 *
 * @author igolovach
 */

public class MethodFrameCounter extends ThreadLocal<AtomicInteger> {

    public final static int MAX_STACK_DEPTH = 256;
    public final static String MSG = "Too deep method frame stack - " + MAX_STACK_DEPTH + ". Perhaps cyclic references in serialized object";

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
            local.get().set(0);
            throw new StackOverflowError(MSG);
        }
    }

    /**
     * Decrement the counter.
     */
    public static void exitFrame() {
        int counter = local.get().decrementAndGet();
        if (counter < 0) {
            local.get().set(0);
            throw new InternalError("Some programming error: count(exitFrame) > count(enterFrame).");
        }
    }
}
