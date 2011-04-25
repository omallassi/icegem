package com.googlecode.icegem.serialization.codegen.impl.primitivearray;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * @author igolovach
 */

public class FromDataFieldPrimitiveArrayProcessor implements FromDataProcessor {
    private final String methodName;

    public FromDataFieldPrimitiveArrayProcessor(String methodName) {
        this.methodName = methodName;
    }

    public String process(XField field) {
        return "result.set" + firstLetterToUpperCase(field.getName()) + "(com.gemstone.gemfire.DataSerializer." + methodName + "(in));\n";
    }
}
