package com.googlecode.icegem.serialization.codegen.impl.system;


import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * User: akondratyev
 */
public class FromDataFieldUUIDProcessor implements FromDataProcessor {
    public String process(XField field) {
        String fieldName = field.getName();
        return "if (in.readByte() != 0) {\n" +
                tab("long mostSignificantBits = in.readLong();\n") +
                tab("long leastSignificantBits = in.readLong();\n") +
                tab("result.set" + firstLetterToUpperCase(fieldName) + "(new java.util.UUID(mostSignificantBits, leastSignificantBits));\n") +
                "}\n";
    }
}
