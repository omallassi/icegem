package com.googlecode.icegem.serialization.codegen.impl.primitive;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

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
