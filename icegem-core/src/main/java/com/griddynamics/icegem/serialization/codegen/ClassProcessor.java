package com.griddynamics.icegem.serialization.codegen;

import static com.griddynamics.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer-s:
 * 1) getId()
 * 2) getSupportedClasses()
 * 3) toData()
 * 4) fromData() 
 * @author igolovach
 */

public class ClassProcessor {

    public String  process(XClass element) {
        StringBuilder builder = new StringBuilder();

        // class header
        builder.append("public class ")
                .append(element.getType().getSimpleName())
                .append("DataSerializer extends com.gemstone.gemfire.DataSerializer {\n");

        // getId()
        builder.append("\n");
        builder.append(tab(new MethodGetIdProcessor().process(element)));

        // getSupportedClasses()
        builder.append("\n");
        builder.append(tab(new MethodGetSupportedClassesProcessor().process(element)));

        // toData()
        builder.append("\n");
        builder.append(tab(new MethodToDataProcessor().process(element)));

        // fromData()
        builder.append("\n");
        builder.append(tab(new MethodFromDataProcessor().process(element)));

        // class end
        builder.append("}");

        return builder.toString();
    }
}
