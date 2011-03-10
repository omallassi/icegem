package com.griddynamics.gemfire.serialization.codegen.impl;

import com.griddynamics.gemfire.serialization.Configuration;
import com.griddynamics.gemfire.serialization.codegen.CodeGenUtils;
import com.griddynamics.gemfire.serialization.codegen.XField;

import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.tab;

/**
 * Generate code that
 * 1) get 'custom class' field value from bean
 * 2) if null -> writeByte(0) + exit
 * 3) if !null writeByte(1) + write field value it to DataOutput
 * for method DataSerializer.fromData(...)
 *
 * @author igolovach
 */

class ToDataFieldResolveClassByGemFireProcessor {

//    public String process(XField field) {
//        String fieldName = field.getField().getName();
//        String shortClassName = field.getField().getType().getSimpleName();
//        String getter = "get" + CodeGenUtils.firstLetterToUpperCase(fieldName) + "()";
//
//        return "if (concrete." + getter + " == null) {\n" +
//                tab("out.writeByte(0);\n") +
//                "} else {\n" +
//                tab("out.writeByte(1);\n" + //todo: CodeGenUtils.DATA_SERIALIZER_PACKAGE + "." + shortClassName -> can be collision between: a.b.bean vs x.y.Bean
//                        "new " + CodeGenUtils.DATA_SERIALIZER_PACKAGE + "." + shortClassName + "DataSerializer()" + //todo: generate full name in one place, not here
//                        ".toData(" +
//                        "concrete." + getter + ", " +
//                        "out);\n") +
//                "}\n";
//    }

    public String process(XField field) {
        String fieldName = field.getName();
        String getter = "get" + CodeGenUtils.firstLetterToUpperCase(fieldName) + "()";

        String allowJavaSerialization = Boolean.toString(Configuration.getCurrent().isJavaSerializationEnabled());

        return "if (concrete." + getter + " == null) {\n" +
                tab("out.writeByte(0);\n") +
                "} else {\n" +
                tab("out.writeByte(1);\n") +
                tab("com.gemstone.gemfire.DataSerializer.writeObject(concrete." + getter + ", out, " + allowJavaSerialization + ");\n") +
                "}\n";
    }
}