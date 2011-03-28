package com.griddynamics.icegem.serialization.codegen.impl.primitivearray;

import com.griddynamics.icegem.serialization.codegen.XField;
import com.griddynamics.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.griddynamics.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * @author igolovach
 */

public class ToDataFieldPrimitiveArrayProcessor implements ToDataProcessor {
    private final String methodName;

    public ToDataFieldPrimitiveArrayProcessor(String methodName) {
        this.methodName = methodName;
    }

    public String process(XField field) {
        return "com.gemstone.gemfire.DataSerializer." + methodName + "(concrete.get" + firstLetterToUpperCase(field.getName()) + "(), out);\n";
    }
}
