package com.griddynamics.gemfire.serialization.codegen.impl.system;

import com.griddynamics.gemfire.serialization.codegen.XField;
import com.griddynamics.gemfire.serialization.codegen.impl.ToDataProcessor;

import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * Generate code that
 * 1) read 'String' field value from bean-setter
 * 2) write to dataOutput
 * for method DataSerializer.toData(...)
 *
 * @author igolovach
 */

public class ToDataFieldStringProcessor implements ToDataProcessor {
    public String process(XField field) {
        String fieldName = field.getName();
        String getter = "get" + firstLetterToUpperCase(fieldName) + "()";
        return "if (concrete." + getter + " == null) {\n" +
                "    out.writeByte(0);\n" +
                "} else {\n" +
                "    out.writeByte(1);\n" +
//                "    out.writeUTF(concrete." + getter + ");\n" +
                "    com.gemstone.gemfire.DataSerializer.writeString(concrete." + getter + ", out);\n" +
                "}\n";
    }
}
