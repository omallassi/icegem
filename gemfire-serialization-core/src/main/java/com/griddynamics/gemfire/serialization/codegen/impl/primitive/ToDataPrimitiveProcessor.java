package com.griddynamics.gemfire.serialization.codegen.impl.primitive;

import com.griddynamics.gemfire.serialization.codegen.XField;
import com.griddynamics.gemfire.serialization.codegen.impl.ToDataProcessor;

import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * @author igolovach
 */

public class ToDataPrimitiveProcessor implements ToDataProcessor {
    private final String dataOutputMethod;

    public ToDataPrimitiveProcessor(String dataOutputMethod) {
        this.dataOutputMethod = dataOutputMethod;
    }

    public String process(XField field) {
        String fieldName = field.getName();
        return "out." + dataOutputMethod + "(concrete.get" + firstLetterToUpperCase(fieldName) + "());\n";
    }
}
