package com.googlecode.icegem.serialization;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.serialization.codegen.DataSerializerGenerator;

import javassist.CannotCompileException;

import java.io.InvalidClassException;
import java.util.*;

/**
 * Responsibility:
 * <p> 1) generate DataSerializers by com.googlecode.icegem.serialization.codegen.DataSerializerGenerator.generateDataSerializerClasses(...)
 * <p> 2) register in GemFire DataSerializers by com.gemstone.gemfire.DataSerializer.register(...)
 * <p> 3) filter enums
 */
public class HierarchyRegistry {

    public static synchronized void registerAll(ClassLoader classLoader, Class<?> ... classArray) throws InvalidClassException, CannotCompileException {
        registerAll(classLoader, Arrays.asList(classArray));
    }

    public static synchronized void registerAll(ClassLoader classLoader, List<Class<?>> classList) throws InvalidClassException, CannotCompileException {
        List<String> list = Arrays.asList("c");
//        List<Class<?>> filteredClassList = new ArrayList<Class<?>>();
//        // filter enum
//        for (Class<?> clazz : classList) {
//            if (Enum.class.isAssignableFrom(clazz)) {
//                // NOP
//            } else {
//                filteredClassList.add(clazz);
//            }
//        }

        //solve problem when one class is registered two or more times
        List<Class<?>> classListToGenerate = new LinkedList<Class<?>>();
        for(Class clazz: classList)
            if (!uniqueClass.contains(clazz)) {
                classListToGenerate.add(clazz);
                uniqueClass.add(clazz);
            }
        // generate classes of DataSerializers
        List<Class<?>> serializerClassList = DataSerializerGenerator.generateDataSerializerClasses(classLoader, /*filteredClassList*/classListToGenerate);

        // register classes of DataSerializers in GemFire
        for (Class<?> clazz : serializerClassList) {
            DataSerializer.register(clazz);
        }
    }
    private static final Set<Class<?>> uniqueClass = new HashSet<Class<?>>();
}
