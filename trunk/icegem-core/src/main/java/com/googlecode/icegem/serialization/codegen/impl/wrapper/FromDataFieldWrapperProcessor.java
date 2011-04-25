package com.googlecode.icegem.serialization.codegen.impl.wrapper;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;


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
