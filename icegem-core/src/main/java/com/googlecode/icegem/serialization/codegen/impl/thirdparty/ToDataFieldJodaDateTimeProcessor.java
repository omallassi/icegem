package com.googlecode.icegem.serialization.codegen.impl.thirdparty;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.ToDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;

/**
 * Created by IntelliJ IDEA.
 * User: volcano
 * Date: 4/7/11
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToDataFieldJodaDateTimeProcessor implements ToDataProcessor{
    public String process(XField field) {
        String fieldName = field.getName();
        String getter = "get" + firstLetterToUpperCase(fieldName) + "()";
        StringBuilder result = new StringBuilder();
        result.append("if (concrete.").append(getter).append(" == null) {\n")
                .append("    out.writeByte(0);\n")
                .append("} else {\n" + "    if (concrete.").append(getter).append(".getClass() != org.joda.time.DateTime.class) {\n")
                .append( "        String wrongClassName = concrete.").append(getter).append(".getClass().getName();\n")
                .append("        throw new com.gemstone.gemfire.ToDataException(\"Field of type org.joda.time.DateTime can contains value only of type org.joda.time.DateTime, not \" + wrongClassName, null);\n")
                .append("    }\n").append("    out.writeByte(1);\n").append("     out.writeUTF(concrete.").append(getter).append(".toDateTime().toString());\n" + "}\n");
        return result.toString();
    }
}
