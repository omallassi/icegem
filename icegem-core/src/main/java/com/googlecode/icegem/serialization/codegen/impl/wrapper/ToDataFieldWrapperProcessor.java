package com.googlecode.icegem.serialization.codegen.impl.wrapper;

import com.googlecode.icegem.serialization.codegen.XProperty;
import com.googlecode.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;


/**
 * @author igolovach
 */

public class ToDataFieldWrapperProcessor implements ToDataProcessor {
    private final String unwrapMethod;
    private final String dataOutputMethod;

    public ToDataFieldWrapperProcessor(String unwrapMethod, String dataOutputMethod) {
        this.unwrapMethod = unwrapMethod;
        this.dataOutputMethod = dataOutputMethod;
    }

    public String process(XProperty field) {
        String fieldName = field.getName();
        String getter = "get" + firstLetterToUpperCase(fieldName) + "()";
        return "if (concrete." + getter + " == null) {\n" +
                "    out.writeByte(0);\n" +
                "} else {\n" +
                "    out.writeByte(1);\n" +
                "    out." + dataOutputMethod + "(concrete." + getter + "." + unwrapMethod + "()" + ");\n" +
                "}\n";
    }
}
