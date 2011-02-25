package com.griddynamics.gemfire.serialization.codegen;

import com.griddynamics.gemfire.serialization.SerializedClass;
import javassist.CannotCompileException;

import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer method getId()
 *
 * @author igolovach
 */

public class MethodGetIdProcessor {

    public String process(XClass element) {

        SerializedClass annotation = element.getType().getAnnotation(SerializedClass.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Called " + MethodGetIdProcessor.class.getSimpleName() + ".process(...) for not serialized class " + element.getType() + " (this class do not marked by @SerializedClass)."); //todo: right ex type?
        }
        int dataSerializerID = annotation.dataSerializerID();

        StringBuilder builder = new StringBuilder();
        builder.append("public int getId() {\n")
                .append(tab("return " + dataSerializerID + ";\n")) 
                .append("}\n");

        return builder.toString();
    }
}
