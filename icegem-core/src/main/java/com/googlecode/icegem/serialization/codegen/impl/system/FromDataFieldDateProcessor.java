package com.googlecode.icegem.serialization.codegen.impl.system;

import com.googlecode.icegem.serialization.codegen.XProperty;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Generate code that
 * 1) read 'java.util.Date' field value from DataInput
 * 2) write to bean-setter
 * for method DataSerializer.fromData(...)
 *
 * @author igolovach
 */

public class FromDataFieldDateProcessor implements FromDataProcessor { //todo: LocalGregorianCalendar
    public String process(XProperty field) {
        String fieldName = field.getName();
        return "if (in.readByte() != 0) {\n" +
                tab("result.set" + firstLetterToUpperCase(fieldName) + "(new java.util.Date(in.readLong()));\n") +
                "}\n";
    }
}
