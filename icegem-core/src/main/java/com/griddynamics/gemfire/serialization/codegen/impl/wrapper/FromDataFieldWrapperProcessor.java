package com.griddynamics.gemfire.serialization.codegen.impl.wrapper;

import com.griddynamics.gemfire.serialization.codegen.XField;
import com.griddynamics.gemfire.serialization.codegen.impl.FromDataProcessor;

import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.tab;
import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;


/**
 * @author igolovach
 */

public class FromDataFieldWrapperProcessor implements FromDataProcessor {
    private final String wrapperClassName;
    private final String dataInputMethod;

    public FromDataFieldWrapperProcessor(String wrapperClassName, String dataInputMethod) {
        this.wrapperClassName = wrapperClassName;
        this.dataInputMethod = dataInputMethod;
    }

    public String process(XField field) {
        String fieldName = field.getName();
        return "if (in.readByte() != 0) {\n" +
                tab("result.set" + firstLetterToUpperCase(fieldName) + "(" + wrapperClassName + ".valueOf(in." + dataInputMethod + "()));\n") +
                "}\n";
    }
}
