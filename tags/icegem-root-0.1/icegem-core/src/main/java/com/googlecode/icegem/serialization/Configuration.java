package com.googlecode.icegem.serialization;

import com.googlecode.icegem.serialization.codegen.impl.ToDataFieldProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object that specify behaviour of framework.
 *
 * @author igolovach
 */

public class Configuration {
    private Set<Class<?>> gemFireResolvedClasses = new HashSet<Class<?>>();

    public Configuration() {
        //todo: more?
        gemFireResolvedClasses.add(Object.class);
        gemFireResolvedClasses.add(Object[].class);
        gemFireResolvedClasses.add(List.class);
        gemFireResolvedClasses.add(Map.class);
        gemFireResolvedClasses.add(Set.class);
    }

    /**
     * Current Configuration: loaded at framework startup.
     * You can cache it: don't reloaded, don't change at runtime.
     */
    public static Configuration getCurrent() {
        return new Configuration(); //todo: load from file
    }

    public boolean isJavaSerializationEnabled() {
        return false;
    }

    public boolean isCustomRegisteredClass(Class<?> clazz) {
        return clazz.getName().startsWith("com.googlecode.gemfire.");
    }

    public boolean useGemFireDataSerializerResolving(Class<?> clazz) { 
        return gemFireResolvedClasses.contains(clazz);
    }

    public boolean serializeByHand(Class<?> clazz) { //todo: rename
        return new ToDataFieldProcessor().map.containsKey(clazz);
    }

    /**
     * All *DataSerializer-s will be created in this package.
     * @return root package
     */
    public String getDataSerializerPackage() {
        return "com.googlecode.icegem.serialization.$$$";
    }
}
