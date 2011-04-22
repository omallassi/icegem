package com.googlecode.icegem.serialization.codegen.impl.system;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * User: akondratyev
 */
public class ToDataFieldTimeStampProcessor implements ToDataProcessor{
    public String process(XField field) {
        String fieldName = field.getName();
        String getter = "get" + firstLetterToUpperCase(fieldName) + "()";
        return "if (concrete." + getter + " == null) {\n" +
                "    out.writeByte(0);\n" +
                "} else {\n" +
                "    if (concrete." + getter + ".getClass() != java.sql.Timestamp.class) {\n" +
                "        String wrongClassName = concrete."+getter+".getClass().getName();\n" +
                "        throw new com.gemstone.gemfire.ToDataException(\"Field of type java.sql.Timestamp can contains value only of type java.sqlTimestamp, not \" + wrongClassName, null);\n" +
                "    }\n" +
                "    out.writeByte(1);\n" +
                "    out.writeLong(concrete." + getter + ".getTime());\n" +
                "}\n";
    }
}
