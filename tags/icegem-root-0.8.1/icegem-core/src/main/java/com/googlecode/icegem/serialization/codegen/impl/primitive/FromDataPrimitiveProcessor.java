package com.googlecode.icegem.serialization.codegen.impl.primitive;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * @author igolovach
 */

public class FromDataPrimitiveProcessor implements FromDataProcessor {
    private final String dataInputMethod;

    public FromDataPrimitiveProcessor(String dataInputMethod) {
        this.dataInputMethod = dataInputMethod;
    }

    public String process(XField field) {
        String fieldName = field.getName();

        return "result.set" + firstLetterToUpperCase(fieldName) + "(in." + dataInputMethod + "());\n";
    }
}
