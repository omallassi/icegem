package com.googlecode.icegem.serialization.codegen;

/**
 * Code generator for DataSerializer method getSupportedClasses()
 * @author igolovach
 */

public class MethodGetSupportedClassesProcessor {

    public String process(XClass element) {
        StringBuilder builder = new StringBuilder();

        builder.append("public Class[] getSupportedClasses() {\n")
                .append(CodeGenUtils.tab("return new Class[]{")).append(element.getType().getName()).append(".class};\n")
                .append("}\n");

        return builder.toString();
    }
}
