package com.griddynamics.icegem.serialization.codegen;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Wrapper class for java.lang.Class + useful methods for code generation
 *
 * @author igolovach
 */

public class XClass {
    private final Class<?> clazz;

    public XClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getType() {
        return clazz;
    }

    public List<XField> getSerialisedSortedFields() {

        final List<XField> result = Introspector.getFields(clazz);

        // all fields ser/deser in predefined order
        Collections.sort(result, new Comparator<XField>() { //todo: probably use Lexicographic order: <parentHeight, fieldName> NOT <parentClassName, fieldName> ?

            public int compare(XField field0, XField field1) {
                return (field0.getDeclaringClass() + ":" + field0.getName()).compareTo(field1.getDeclaringClass() + ":" + field1.getName()); //todo: is it unique name?
            }
        });
        //todo: field from super class here

        return result;
    }
}
