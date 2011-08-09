package com.googlecode.icegem.serialization.codegen.impl.system;

import com.googlecode.icegem.serialization.codegen.XProperty;
import com.googlecode.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * Generate code that
 * 1) read 'String' field value from bean-setter
 * 2) write to dataOutput
 * for method DataSerializer.toData(...)
 *
 * @author igolovach
 */

public class ToDataFieldStringProcessor implements ToDataProcessor {
    public String process(XProperty field) {
        String fieldName = field.getName();
        String getter = "get" + firstLetterToUpperCase(fieldName) + "()";
        return "com.gemstone.gemfire.DataSerializer.writeString(concrete." + getter + ", out);\n";
    }
}