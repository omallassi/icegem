package com.googlecode.icegem.serialization.codegen.impl.system;

import com.googlecode.icegem.serialization.codegen.XProperty;
import com.googlecode.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * Logic copied from Hessian: com.caucho.hessian.io.CalendarHandle
 *
 * @author igolovach
 */

public class ToDataFieldCalendarProcessor implements ToDataProcessor {
    public String process(XProperty field) {
        String fieldName = field.getName();
        String getter = "get" + firstLetterToUpperCase(fieldName) + "()";
        return "if (concrete." + getter + " == null) {\n" +
                "    out.writeByte(0);\n" +
                "} else {\n" +
                "    out.writeByte(1);\n" +
                "    out.writeUTF(concrete." + getter + ".getClass().getName());\n" +
                "    out.writeLong(concrete." + getter + ".getTimeInMillis());\n" +
                "}\n";
    }
}