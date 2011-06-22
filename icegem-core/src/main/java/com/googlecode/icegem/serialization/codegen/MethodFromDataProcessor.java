package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.codegen.impl.FromDataFieldProcessor;

import java.util.List;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer method fromData()
 *
 * @author igolovach
 */

public class MethodFromDataProcessor {

    public String process(XClass element) {
        if (Enum.class.isAssignableFrom(element.getType())) {
            if (element.getType() == Enum.class) {
                throw new RuntimeException("Never here!"); //todo: correct ex? more info?
            } else {
                return processEnum(element);
            }
        } else {
            return processNotEnum(element);
        }
    }

    private String processNotEnum(XClass element) {
        StringBuilder builder = new StringBuilder();

        List<XField> fields = element.getSerialisedSortedFields();
        final String className = element.getType().getName();
        int currentBeanVersion = element.getType().getAnnotation(BeanVersion.class).value();
        byte currentVersionHistoryLength = (byte) (element.getType().getAnnotation(AutoSerializable.class).versionHistoryLength() + 1);
        // method header
        builder.append("public Object fromData(java.io.DataInput in) throws java.io.IOException, ClassNotFoundException {\n")
                .append(tab("// checks bean version\n"))
                .append(tab("int currentVersion = " + currentBeanVersion + "; \n"))
                .append(tab("int actualVersion = in.readInt();\n"))
                .append(tab("if (currentVersion < actualVersion) {\n"))
                .append(tab(2, "throw new ClassCastException(\"Current bean version is less than serialized: "))
                .append("current is '\" + currentVersion + \"', actual is '\" + actualVersion + \"'\");\n")
                .append(tab("}\n"))
                .append("\n");
        // TODO: header version will be used in future to implement different serialization/deserialization strategies
        builder.append(tab("// checks header version\n"))
                .append(tab("byte currentHeaderVersion = " + element.getType().getAnnotation(AutoSerializable.class).headerVersion() +"; \n"))
                .append(tab("byte actualHeaderVersion = in.readByte();\n"))
                .append(tab("if (currentHeaderVersion != actualHeaderVersion) {\n"))
                .append(tab(2, "throw new ClassCastException(\"Current header version of the class differs with serialized: "))
                .append("current is '\" + currentHeaderVersion + \"', actual is '\" + actualHeaderVersion + \"'\");\n")
                .append(tab("}\n"))
                .append("\n");

        int currentStartFromVersion = (currentBeanVersion - currentVersionHistoryLength + 1) > 1
                ? (currentBeanVersion - currentVersionHistoryLength + 1) : 1;

        builder.append(tab("// stores current class model cache codes for bean versions [" +
                currentStartFromVersion + ", " + currentBeanVersion + "]\n"))
                .append(tab("java.util.Map currentModelHashCodesByBeanVersions = new java.util.HashMap(" +
                        (currentBeanVersion - currentStartFromVersion + 1) + ");\n"));

        for (int i = currentStartFromVersion; i <= currentBeanVersion; i++) {
            builder.append(tab("currentModelHashCodesByBeanVersions.put(new Integer("
                    + i + "), new Integer(" + CodeGenUtils.getClassModelHashCodeBasedOnClassFields(fields, i) + "));\n"));
        }
        builder.append("\n");

        builder.append(tab("// checks class model hash codes\n"))
                .append(tab("byte actualVersionHistoryLength = in.readByte();\n"))
                .append(tab("int actualStartFromVersion = (actualVersion - actualVersionHistoryLength + 1) > 1 ? (actualVersion - actualVersionHistoryLength + 1) : 1;\n"))
                .append(tab("for (int i = actualStartFromVersion; i <= actualVersion; i++) {\n"))
                .append(tab(2, "Integer currentClassModelHashCode = (Integer) currentModelHashCodesByBeanVersions.get(new Integer(i));\n"))
                .append(tab(2, "if (currentClassModelHashCode == null) continue;\n"))
                .append(tab(2, "int actualClassModelHashCode = in.readInt();\n"))
                .append(tab(2, "if (currentClassModelHashCode.intValue() != actualClassModelHashCode) {\n"))
                .append(tab(3, "throw new ClassCastException(\"Model of the current class does not match with the model " +
                        "of the serialized class. Maybe you have forgotten to increase value of @BeanVersion annotation or mark " +
                        "newly added fields with @FieldVersion annotation. Or you have modified or deleted already existed in previous bean versions fields. " +
                        "Inconsistency has been found for bean version '\" + i + \"'\");\n"))
                .append(tab(2,"}\n"))
                .append(tab("}\n"))
                .append("\n");

        builder.append(tab("// create 'empty' bean\n"))
                .append(tab(className + " result = new " + className + "();\n")); //todo: no-arg constructor

        for (XField field : fields) {
            if (field.getFieldVersion() > 1) {
                builder.append(tab("if (actualVersion == currentVersion || actualVersion >= " + field.getFieldVersion() + ") {"));
                attachFieldReader(builder, field);
                builder.append(tab("}"));
            }   else {
                attachFieldReader(builder, field);
            }
        }
        builder.append("\n");

        // method end
        builder.append(tab("// return 'full' bean\n"));
        builder.append(tab("return result;\n"))
                .append("}\n");
        return builder.toString();
    }

    private static void attachFieldReader(StringBuilder builder, XField field) {
        builder.append("\n");
        builder.append(tab("// byte[] -> this." + field.getName() + "\n")); //todo: can be collision between parent/child field names
        builder.append(tab(new FromDataFieldProcessor().process(field)));
    }

    private String processEnum(XClass element) {
        StringBuilder builder = new StringBuilder();

        final String className = element.getType().getName();
        builder.append("public Object fromData(java.io.DataInput in) throws java.io.IOException, ClassNotFoundException {\n")
                .append(tab("// read only 'name'\n"))
                .append(tab("return Enum.valueOf(" + className + ".class, in.readUTF());\n")) //todo: need int / short_int
                .append("}\n");

        return builder.toString();
    }
}