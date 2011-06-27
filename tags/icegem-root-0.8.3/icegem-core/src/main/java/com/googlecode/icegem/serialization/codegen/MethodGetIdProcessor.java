package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.AutoSerializable;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer method getId()
 *
 * @author igolovach
 */

public class MethodGetIdProcessor {

    public String process(XClass element) {

        AutoSerializable annotation = element.getType().getAnnotation(AutoSerializable.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Called " + MethodGetIdProcessor.class.getSimpleName() + ".process(...) for not serialized class " + element.getType() + " (this class do not marked by @AutoSerializable)."); //todo: right ex type?
        }
        int dataSerializerID = annotation.dataSerializerID();

        StringBuilder builder = new StringBuilder();
        builder.append("public int getId() {\n")
                .append(tab("return " + dataSerializerID + ";\n")) 
                .append("}\n");

        return builder.toString();
    }
}
