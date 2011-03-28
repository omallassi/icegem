package com.griddynamics.icegem.serialization.codegen;

import java.util.List;

/**
 * Wrapper class for java.lang.reflect.Field + useful methods for code generation
 *
 * @author igolovach
 */

public class XField { //todo: check field is serialized? (like in XClass constructor)

    private final String name;
    private final Class<?> type;
    private final Class<?> declaringClass;
    private final List annotations;
    private final int fieldVersion;

    public XField(String name, Class<?> type, Class<?> declaringClass, List annotations, int fieldVersion) {
        this.name = name;
        this.type = type;
        this.declaringClass = declaringClass;
        this.annotations = annotations;
        this.fieldVersion = fieldVersion;
    }

    public List getAnnotations() {
        return annotations;
    }

    public int getFieldVersion() {
        return fieldVersion;
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
