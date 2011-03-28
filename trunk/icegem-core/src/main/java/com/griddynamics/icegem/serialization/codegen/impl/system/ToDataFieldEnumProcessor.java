package com.griddynamics.icegem.serialization.codegen.impl.system;

import com.griddynamics.icegem.serialization.codegen.XField;
import com.griddynamics.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.griddynamics.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * Seserialize field of type concrete successor of Enum but not Enum.
 *
 * @author igolovach
 */

public class ToDataFieldEnumProcessor implements ToDataProcessor { //todo: you can pack enum very well (only ordinal, not class name)
    //todo: need write null/notnull flag if we use DataSerializer.writeEnum?
    //todo: how GemFire serialize enum? what if it contains mutable fields? enum {int s; getA(){ret s} setB(arg) {s = arg}}
    // todo: warn if enum contains mutable fields?

    public String process(XField field) {
        String fieldName = field.getName();
        String getter = "get" + firstLetterToUpperCase(fieldName) + "()";
        return "if (concrete." + getter + " == null) {\n" +
                "    out.writeByte(0);\n" +
                "} else {\n" +
                "    out.writeByte(1);\n" +
                "    out.writeUTF(concrete." + getter + ".getClass().getName());\n" +
                "    com.gemstone.gemfire.DataSerializer.writeEnum(concrete." + getter + ", out);\n" +
                "}\n";
    }
}