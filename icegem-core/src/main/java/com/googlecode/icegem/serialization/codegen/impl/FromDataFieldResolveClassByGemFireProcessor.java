package com.googlecode.icegem.serialization.codegen.impl;

import com.googlecode.icegem.serialization.codegen.CodeGenUtils;
import com.googlecode.icegem.serialization.codegen.XProperty;

/**
 * Generate code that
 * 1) read 'custom class' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 *
 * @author igolovach
 */

class FromDataFieldResolveClassByGemFireProcessor {

//    public String process(XProperty field) {
//        String fieldName = field.getField().getName();
//        String fullClassName = field.getField().getType().getName();
//        String shortClassName = field.getField().getType().getSimpleName();
//
//        return "if (in.readByte() != 0) {\n" +
//                CodeGenUtils.tab("result.set" + CodeGenUtils.firstLetterToUpperCase(fieldName) + "(" +
//                        "(" + fullClassName + ") " +
//                        "new " + CodeGenUtils.DATA_SERIALIZER_PACKAGE + "." + shortClassName + "DataSerializer().fromData(in)" +
//                        ");\n")
//                + "}\n";
//    }

    public String process(XProperty field) {
        String fieldName = field.getName();
        String fullClassName = CodeGenUtils.className(field.getType());

        return "if (in.readByte() != 0) {\n" +
                CodeGenUtils.tab("result.set" + CodeGenUtils.firstLetterToUpperCase(fieldName) + "(" +
                        "(" + fullClassName + ") " +
                        "com.gemstone.gemfire.DataSerializer.readObject(in)" +
                        ");\n")
                + "}\n";
    }
}
