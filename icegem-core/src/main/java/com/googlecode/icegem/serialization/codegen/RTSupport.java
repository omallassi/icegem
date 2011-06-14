package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.codegen.exception.MethodFrameStackOverflowException;

import java.io.InvalidClassException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import static java.util.Arrays.asList;

/**
 * @author igolovach
 */
// TODO: Complete this class.
public class RTSupport {

    private static final Set<Class<?>> JDK_ALLOWED_CLASSES;

    static {
        //todo:
        //        JDK_ALLOWED_CLASSES.add(BigInteger.class);
        //        JDK_ALLOWED_CLASSES.add(BigDecimal.class);
        //        //
        //        JDK_ALLOWED_CLASSES.add(Calendar.class);
        //        JDK_ALLOWED_CLASSES.add(Locale.class);
        //        JDK_ALLOWED_CLASSES.add(TimeZone.class);
        //        JDK_ALLOWED_CLASSES.add(GregorianCalendar.class);
        //        //
        //        JDK_ALLOWED_CLASSES.add(Currency.class);
        //        JDK_ALLOWED_CLASSES.add(UUID.class);
        //        JDK_ALLOWED_CLASSES.add(URL.class);
        //        JDK_ALLOWED_CLASSES.add(URI.class);
        JDK_ALLOWED_CLASSES = new HashSet<Class<?>>();
        //
        JDK_ALLOWED_CLASSES.addAll(asList(byte.class, short.class, char.class, int.class, long.class, float.class, double.class));
        JDK_ALLOWED_CLASSES.addAll(asList(Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class));
        //
        JDK_ALLOWED_CLASSES.addAll(asList(String.class, Date.class));
        //
        JDK_ALLOWED_CLASSES.addAll(asList(ArrayList.class, LinkedList.class, HashMap.class, TreeMap.class, IdentityHashMap.class, LinkedHashSet.class, HashSet.class, TreeSet.class));
        //
        JDK_ALLOWED_CLASSES.addAll(asList(Hashtable.class, Vector.class, Stack.class));
    }

    public static void checkAllowedInCompileTime(Class<?> clazz) throws InvalidClassException {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        if (clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.")) {
            checkAllowedInCompileTimeJdkType(clazz);
        } else {
            checkAllowedInCompileTimeCustomType(clazz);
        }
    }

    private static void checkAllowedInCompileTimeJdkType(Class<?> clazz) throws InvalidClassException {
        if (!JDK_ALLOWED_CLASSES.contains(clazz)) {
            throw new InvalidClassException(clazz.getName() + " is disallowed, only " + JDK_ALLOWED_CLASSES);
        }
    }

    private static void checkAllowedInCompileTimeCustomType(Class<?> clazz) {
        //To change body of created methods use File | Settings | File Templates.
        // TODO: Write method body
    }

    public static void checkAllowedInRealTime(Object obj) throws InvalidClassException {
        try {
            checkAllowedInRealTime0(obj, 1); //todo: correct 1? or 0?
        } catch (MethodFrameStackOverflowException e) {
            throw new MethodFrameStackOverflowException(MethodFrameCounter.STACK_OVERFLOW_MSG + ". For object: " + obj.toString());
        }
    }

    /**
     * With stack counter
     *
     * @param obj
     * @param depth of
     * @throws InvalidClassException
     */
    private static void checkAllowedInRealTime0(Object obj, int depth) throws InvalidClassException {
        if (depth >= MethodFrameCounter.MAX_STACK_DEPTH) { //todo: correct >? or >=?
            throw new MethodFrameStackOverflowException();
        }
        Class<?> clazz = obj.getClass();

        if (clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.")) {
            checkAllowedInCompileTimeJdkType(clazz);
        } else {
            checkAllowedInCompileTimeCustomType(clazz);
        }        

        // array
        if (clazz.isArray()) {
            final int length = Array.getLength(obj);
            for (int k = 0; k < length; k++) {
                Object elem = Array.get(obj, k);
                checkAllowedInRealTime0(elem, depth + 1);
            }
        }
        // Collection
        if (Collection.class.isAssignableFrom(clazz)) {
            for (Object elem : ((Collection) obj)) {
                checkAllowedInRealTime0(elem, depth + 1);
            }
        }
        // Map
        if (Map.class.isAssignableFrom(clazz)) {
            for (Map.Entry<Object, Object> elem : ((Map<Object, Object>) obj).entrySet()) {
                checkAllowedInRealTime0(elem.getKey(), depth + 1);
                checkAllowedInRealTime0(elem.getValue(), depth + 1);
            }
        }
    }
}
