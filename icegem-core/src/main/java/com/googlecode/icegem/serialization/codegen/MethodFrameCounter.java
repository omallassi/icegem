package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.codegen.exception.IceGemRuntimeException;
import com.googlecode.icegem.serialization.codegen.exception.MethodFrameStackOverflowException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Analog of javassist.runtime.Cflow
 *
 * @author igolovach
 * @author Andrey Stepanov aka standy 
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

    private static final ThreadLocal<List<String>> classNames = new ThreadLocal<List<String>>() {
        protected synchronized List<String> initialValue() {
            return new ArrayList<String>();
        }
    };

    /**
     * Increment the counter.
     * @param className name of class to enter.
     */
    public static void enterFrame(String className) {
        int counter = local.get().incrementAndGet();
        classNames.get().add(className);
        if (counter == MAX_STACK_DEPTH) {
            throw new MethodFrameStackOverflowException(STACK_OVERFLOW_MSG + getClassNames());
        }
    }

    /**
     * Decrement the counter and remove class name from the list.
     * @param className name of class to exit from.
     */
    public static void exitFrame(String className) {
        int counter = local.get().decrementAndGet();
        if (counter < 0) {
            String errorMessage = "Method frame counter is less then 0. Some programming error: count(exitFrame) > count(enterFrame)."
                    + getClassNames();
            clearCounter();
            throw new IceGemRuntimeException(errorMessage);
        }
        String frameToExit = classNames.get().remove(classNames.get().size() - 1);
        if (!className.equals(frameToExit)) {
            throw new IceGemRuntimeException("Method frame counter try to exit from the class '" + className
                    + "' but must exit from the class '" + frameToExit + "' first." + getClassNames());
        }
    }


    /**
     * Clear the counter and list of class names.
     */
    private static void clearCounter() {
        local.get().set(0);
        classNames.get().clear();
    }

    /**
     * Creates string with all classes that have been entered by method frame counter.
     * @return String
     */
    private static String getClassNames() {
        StringBuilder result = new StringBuilder("\nMethod frame counter enter to the following classes:\n");
        for (String className : classNames.get()) {
            result.append(className).append("\n");
        }
        return result.toString();
    }
}
