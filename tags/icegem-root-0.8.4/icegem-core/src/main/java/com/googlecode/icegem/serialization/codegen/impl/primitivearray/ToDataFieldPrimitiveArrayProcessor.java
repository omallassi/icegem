package com.googlecode.icegem.serialization.codegen.impl.primitivearray;

import com.googlecode.icegem.serialization.codegen.XProperty;
import com.googlecode.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * @author igolovach
 */

public class ToDataFieldPrimitiveArrayProcessor implements ToDataProcessor {
    private final String methodName;

    public ToDataFieldPrimitiveArrayProcessor(String methodName) {
        this.methodName = methodName;
    }

    public String process(XProperty field) {
        return "com.gemstone.gemfire.DataSerializer." + methodName + "(concrete.get" + firstLetterToUpperCase(field.getName()) + "(), out);\n";
    }
}
