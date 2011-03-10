package com.griddynamics.gemfire.serialization.codegen;

import com.griddynamics.gemfire.serialization.codegen.impl.FromDataFieldProcessor;

import java.util.List;

import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer method fromData()
 *
 * @author igolovach
 */

public class MethodFromDataProcessor {

    public String process(XClass element) {
        if (Enum.class.isAssignableFrom(element.getType())) {
            if (element.getType() == Enum.class) {
                throw new InternalError("Never here!"); //todo: correct ex? more info?
            } else {
                return processEnum(element);
            }
        } else {
            return processNotEnum(element);
        }
    }

    private String processNotEnum(XClass element) {
        StringBuilder builder = new StringBuilder();

        final String className = element.getType().getName();
        // method header
        builder.append("public Object fromData(java.io.DataInput in) throws java.io.IOException, ClassNotFoundException {\n")
                .append(tab("// create 'empty' bean\n"))
                .append(tab(className + " result = new " + className + "();\n")); //todo: no-arg constructor

        List<XField> fields = element.getSerialisedSortedFields();
        for (XField field : fields) {
            builder.append("\n");
            builder.append(tab("// byte[] -> this." + field.getName() + "\n")); //todo: can be collision between parent/child field names
            builder.append(tab(new FromDataFieldProcessor().process(field)));
        }
        builder.append("\n");

        // method end
        builder.append(tab("// return 'full' bean\n"));
        builder.append(tab("return result;\n"))
                .append("}\n");

        return builder.toString();
    }


    private String processEnum(XClass element) {
        StringBuilder builder = new StringBuilder();
        
        final String className = element.getType().getName();
        builder.append("public Object fromData(java.io.DataInput in) throws java.io.IOException, ClassNotFoundException {\n")
                .append(tab("// read only 'name'\n"))
                .append(tab("return Enum.valueOf(" + className + ".class, in.readUTF());\n")) //todo: need int / short_int
                .append("}\n");

        return builder.toString();
    }
}