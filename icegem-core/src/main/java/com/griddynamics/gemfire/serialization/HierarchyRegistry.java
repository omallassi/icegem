package com.griddynamics.gemfire.serialization;

import com.gemstone.gemfire.DataSerializer;
import com.griddynamics.gemfire.serialization.codegen.DataSerializerGenerator;
import javassist.CannotCompileException;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.List;

/**
 * Responsibility:
 * <p> 1) generate DataSerializers by com.griddynamics.gemfire.serialization.codegen.DataSerializerGenerator.generateDataSerializerClasses(...)
 * <p> 2) register in GemFire DataSerializers by com.gemstone.gemfire.DataSerializer.register(...)
 * <p> 3) filter enums
 */
public class HierarchyRegistry {

    public static synchronized void registerAll(ClassLoader classLoader, Class<?> ... classArray) throws InvalidClassException, CannotCompileException {
        registerAll(classLoader, Arrays.asList(classArray));
    }

    public static synchronized void registerAll(ClassLoader classLoader, List<Class<?>> classList) throws InvalidClassException, CannotCompileException {

//        List<Class<?>> filteredClassList = new ArrayList<Class<?>>();
//        // filter enum
//        for (Class<?> clazz : classList) {
//            if (Enum.class.isAssignableFrom(clazz)) {
//                // NOP
//            } else {
//                filteredClassList.add(clazz);
//            }
//        }

        // generate classes of DataSerializers
        List<Class<?>> serializerClassList = DataSerializerGenerator.generateDataSerializerClasses(classLoader, /*filteredClassList*/classList);

        // register classes of DataSerializers in GemFire
        for (Class<?> clazz : serializerClassList) {
            DataSerializer.register(clazz);
        }
    }
}
