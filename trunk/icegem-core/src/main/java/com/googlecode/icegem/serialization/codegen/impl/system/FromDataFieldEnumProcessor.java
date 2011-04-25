package com.googlecode.icegem.serialization.codegen.impl.system;

import com.googlecode.icegem.serialization.codegen.XField;
import com.googlecode.icegem.serialization.codegen.impl.FromDataProcessor;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Deserialize field of type concrete successor of Enum but not Enum.
 *
 * @author igolovach
 */

public class FromDataFieldEnumProcessor implements FromDataProcessor {
    public String process(XField field) {
        String fieldName = field.getName();
        String className = field.getType().getName();
        return "if (in.readByte() != 0) {\n" +
                tab("String className = in.readUTF();\n") +
                tab("Class clazz = Class.forName(className);\n") + //todo: which ClassLoader to use?
                tab("result.set" + firstLetterToUpperCase(fieldName) + "(com.gemstone.gemfire.DataSerializer.readEnum(clazz, in));\n") +
                "}\n";
    }
}
