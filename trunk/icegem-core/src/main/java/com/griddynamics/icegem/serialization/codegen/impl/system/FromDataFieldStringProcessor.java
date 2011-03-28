package com.griddynamics.icegem.serialization.codegen.impl.system;

import com.griddynamics.icegem.serialization.codegen.XField;
import com.griddynamics.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.griddynamics.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.griddynamics.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Generate code that
 * 1) read 'String' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 *
 * @author igolovach
 */

public class FromDataFieldStringProcessor implements FromDataProcessor {
    public String process(XField field) {
        String fieldName = field.getName();
        return "if (in.readByte() != 0) {\n" +
//                tab("result.set" + firstLetterToUpperCase(fieldName) + "(in.readUTF());\n") +
                tab("result.set" + firstLetterToUpperCase(fieldName) + "(com.gemstone.gemfire.DataSerializer.readString(in));\n") +
                "}\n";
    }
}
