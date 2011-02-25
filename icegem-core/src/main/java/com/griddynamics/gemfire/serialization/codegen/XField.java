package com.griddynamics.gemfire.serialization.codegen;

/**
 * Wrapper class for java.lang.reflect.Field + useful methods for code generation
 *
 * @author igolovach
 */

public class XField { //todo: check field is serialized? (like in XClass constructor)

    private final String name;
    private final Class<?> type;
    private final Class<?> declaringClass;

    public XField(String name, Class<?> type, Class<?> declaringClass) {
        this.name = name;
        this.type = type;
        this.declaringClass = declaringClass;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    //todo: add getGetterName + getSetterName

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }
}
