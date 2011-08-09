package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.codegen.impl.ToDataFieldProcessor;
import sun.awt.image.OffScreenImage;
import sun.rmi.runtime.NewThreadAction;

import java.util.List;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer method toData()
 *
 * @author igolovach
 */

public class MethodToDataProcessor {

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
        // method header
        builder.append("public boolean toData(Object obj, java.io.DataOutput out) throws java.io.IOException {\n")
                .append(tab("try {\n"))
                .append(tab(2, "// check arg is of correct type\n"))
                .append(tab(2, "if (obj.getClass() != " + className + ".class) {return false;}\n"));
        // add check for cycles using method frame counter
        builder.append(tab(2, "// increment thread-local method-frame counter\n"))
                // todo: not call if fields - not beans! for example - primitives
                // todo: analyze - if exception - we clean counter?
                .append(tab(2, "if (Boolean.getBoolean(com.googlecode.icegem.serialization.codegen.MethodFrameCounter.SYSTEM_PROPERTY_NAME)) {\n"))
                .append(tab(3, "com.googlecode.icegem.serialization.codegen.MethodFrameCounter.enterFrame(\"" + className + "\");\n")) //todo: analize exist MethodFrameCounter.class in runtime
                .append(tab(2, "}\n"))

                .append(tab(2, "// convert to concrete type\n"))
                .append(tab(2, className)).append(" concrete = (").append(className).append(") obj;\n");

        builder.append("\n");

        int beanVersion;

        if (element.getType().getAnnotation(BeanVersion.class) != null) {
            beanVersion = element.getType().getAnnotation(BeanVersion.class).value();
            // checks on positive value
            if (beanVersion < 1) {
                throw new RuntimeException("Value of annotation @BeanVersion must be positive, current value = " + beanVersion + " (class '" + className + "')");
            }

            builder.append(tab(2, "// write bean version\n"))
                    .append(tab(2, "out.writeInt(" + element.getType().getAnnotation(BeanVersion.class).value() + ");\n"));  //todo: value is hardcoded in result code
        } else {
            throw new RuntimeException("Class must be annotated with @BeanVersion: " + element.getType().getCanonicalName());
        }
        builder.append("\n");

        // write header version and version history lenght
        byte headerVersion = element.getType().getAnnotation(AutoSerializable.class).headerVersion();
        byte versionHistoryLength = element.getType().getAnnotation(AutoSerializable.class).versionHistoryLength();

        if (headerVersion < 1) {
            throw new RuntimeException("Class header version of annotation @AutoSerializable must be positive, current value = " + headerVersion + " (class '" + className + "')");
        }
        if (versionHistoryLength < 1) {
            throw new RuntimeException("Version history length of annotation @AutoSerializable must be positive, current value = " + versionHistoryLength + " (class '" + className + "')");
        }
        versionHistoryLength = (byte) (versionHistoryLength + 1);

        builder.append(tab(2, "// write header version and version history lenght\n"));
        // TODO: header version will be used in future to implement different serialization/deserialization strategies
        builder.append(tab(2, "out.writeByte(" + headerVersion + ");\n"))
                .append(tab(2, "out.writeByte(" + versionHistoryLength + ");\n"));

        int startFromVersion = (beanVersion - versionHistoryLength + 1) > 1 ? (beanVersion - versionHistoryLength + 1) : 1;
        builder.append(tab(2, "// write class model hash codes for bean versions [" + startFromVersion + ", " + beanVersion + "]\n"));
        for (int i = startFromVersion; i <= beanVersion; i++) {
            builder.append(tab(2, "out.writeInt(" + CodeGenUtils.getClassModelHashCodeBasedOnClassFields(fields, i) +");\n"));
        }

        for (XField field : fields) {
            builder.append("\n");
            builder.append(tab(2, "// this." + field.getName() + " -> byte[]\n")); //todo: can be name collision between parent/child fields
            builder.append(tab(2, new ToDataFieldProcessor().process(field)));
        }
        builder.append("\n");

        // method end
        builder.append(tab(2, "return true;\n"))
                // ensure that exit frame will be called
                .append(tab("} finally {\n"))
                .append(tab(2, "if (Boolean.getBoolean(com.googlecode.icegem.serialization.codegen.MethodFrameCounter.SYSTEM_PROPERTY_NAME)) {\n"))
                .append(tab(3, "// decrement thread-local method-frame counter\n"))
                .append(tab(3, "com.googlecode.icegem.serialization.codegen.MethodFrameCounter.exitFrame(\"" + className + "\");\n"))
                .append(tab(2, "}\n"))

                .append(tab("}\n"))
                .append("}\n");

        return builder.toString();
    }

    private String processEnum(XClass element) {

        StringBuilder builder = new StringBuilder();

        final String className = element.getType().getName();
        // method header
        builder.append("public boolean toData(Object obj, java.io.DataOutput out) throws java.io.IOException {\n")
                .append(tab("// check arg is of correct type\n"))
                .append(tab("if (obj.getClass() != " + className + ".class) {return false;}\n"))
                .append(tab("// convert to concrete type\n"))
                .append(tab(className)).append(" concrete = (").append(className).append(") obj;\n");

        builder.append(tab("// write only 'name'\n"));
        builder.append(tab("out.writeUTF(concrete.name());\n")); //todo: int/short_int?

        // method end
        builder.append(tab("return true;\n"))
                .append("}\n");

        return builder.toString();
    }
}