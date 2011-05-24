package com.googlecode.icegem.serialization;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.serialization.codegen.DataSerializerGenerator;
import com.googlecode.icegem.serialization.serializers.RegisteredDataSerializers;

/**
 * Responsibility: <p> 1) generate DataSerializers by
 * com.googlecode.icegem.serialization.codegen.DataSerializerGenerator.generateDataSerializerClasses(...) <p> 2)
 * register in GemFire DataSerializers by com.gemstone.gemfire.DataSerializer.register(...) <p> 3) filter enums
 */
public class HierarchyRegistry {

    private static Logger logger = LoggerFactory.getLogger(HierarchyRegistry.class);

    public static synchronized void registerAll(ClassLoader classLoader, Class<?>... classArray)
            throws InvalidClassException, CannotCompileException {
        registerAll(classLoader, Arrays.asList(classArray), null);
    }

    public static synchronized void registerAll(ClassLoader classLoader, List<Class<?>> classList)
            throws InvalidClassException, CannotCompileException {
        registerAll(classLoader, classList, null);
    }

    public static synchronized void registerAll(ClassLoader classLoader, List<Class<?>> classList, String outputDir)
            throws InvalidClassException, CannotCompileException {
        List<String> list = Arrays.asList("c");

        // solve problem when one class is registered two or more times
        List<Class<?>> classListToGenerate = new LinkedList<Class<?>>();
        for (Class clazz : classList) {
            if (!uniqueClass.contains(clazz)) {
                classListToGenerate.add(clazz);
                uniqueClass.add(clazz);
            } else {
                logger.warn("try to register class \'{}\' once more", clazz);
            }
        }
        // generate classes of DataSerializers
        List<Class<?>> serializerClassList = DataSerializerGenerator.generateDataSerializerClasses(classLoader, /* filteredClassList */
                classListToGenerate, outputDir);

        // register classes of DataSerializers in GemFire
        for (Class<?> clazz : serializerClassList) {
            DataSerializer.register(clazz);
        }

        registerDataSerializers();
    }

    public static void registerDataSerializers() {
        for (Class<?> clazz : RegisteredDataSerializers.getDataSerializers()) {
            DataSerializer.register(clazz);
        }
    }

    private static final Set<Class<?>> uniqueClass = new HashSet<Class<?>>();
}
