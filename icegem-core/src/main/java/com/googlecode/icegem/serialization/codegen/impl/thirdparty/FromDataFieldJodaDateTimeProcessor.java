package com.googlecode.icegem.serialization.codegen.impl.thirdparty;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * User: akondratyev
 */
public class FromDataFieldJodaDateTimeProcessor implements FromDataProcessor{
    public String process(XField field) {
        String fieldName = field.getName();
        return "if (in.readByte() != 0) {\n" +
                tab("result.set" + firstLetterToUpperCase(fieldName) + "(new org.joda.time.DateTime(in.readUTF()));\n") +
                "}\n";
    }
}
