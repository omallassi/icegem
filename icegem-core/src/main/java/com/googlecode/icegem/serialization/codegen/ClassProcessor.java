package com.googlecode.icegem.serialization.codegen;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer-s:
 * 1) getId()
 * 2) getSupportedClasses()
 * 3) toData()
 * 4) fromData() 
 * @author igolovach
 */

public class ClassProcessor {

    public String process(XClass xClass, String serializerClsName) {
        StringBuilder builder = new StringBuilder();
        
        // class header
        builder.append("public class ")
                .append(xClass.getType().getSimpleName())
                .append("DataSerializer extends com.gemstone.gemfire.DataSerializer {\n");

        builder.append(tab("public static final com.googlecode.icegem.serialization.codegen.VersionMap VERSION_METADATA;"));
        
        builder.append(new StaticConstructorGenerator().process(xClass, serializerClsName));
        builder.append("}\n");
        
        // getId()
        builder.append("\n");
        builder.append(tab(new MethodGetIdProcessor().process(xClass)));

        // getSupportedClasses()
        builder.append("\n");
        builder.append(tab(new MethodGetSupportedClassesProcessor().process(xClass)));

        // toData()
        builder.append("\n");
        builder.append(tab(new MethodToDataProcessor().process(xClass)));

        // fromData()
        builder.append("\n");
        builder.append(tab(new MethodFromDataProcessor().process(xClass)));

        // class end
        builder.append("}");

        return builder.toString();
    }
}
