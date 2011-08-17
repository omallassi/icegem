package com.googlecode.icegem.serialization.codegen.impl.system;

import com.googlecode.icegem.serialization.codegen.XProperty;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Generate code that
 * 1) read 'String' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 *
 * @author igolovach
 */

public class FromDataFieldStringProcessor implements FromDataProcessor {
    public String process(XProperty field) {
        String fieldName = field.getName();
        return tab("result.set" + firstLetterToUpperCase(fieldName) + "(com.gemstone.gemfire.DataSerializer.readString(in));\n");
    }
}
