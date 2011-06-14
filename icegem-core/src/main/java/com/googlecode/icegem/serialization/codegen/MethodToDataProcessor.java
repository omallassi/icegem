package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.codegen.exception.IceGemRuntimeException;
import com.googlecode.icegem.serialization.codegen.impl.ToDataFieldProcessor;

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
                throw new IceGemRuntimeException("Never here!"); //todo: correct ex? more info?
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
                .append(tab(3, "com.googlecode.icegem.serialization.codegen.MethodFrameCounter.enterFrame();\n")) //todo: analize exist MethodFrameCounter.class in runtime
                .append(tab(2, "}\n"))

                .append(tab(2, "// convert to concrete type\n"))
                .append(tab(2, className)).append(" concrete = (").append(className).append(") obj;\n");

        builder.append("\n");

        //save bean value
        if (element.getType().getAnnotation(BeanVersion.class) != null) {
            builder.append(tab(2, "out.writeInt(" + element.getType().getAnnotation(BeanVersion.class).value()+");\n"));  //todo: value is hardcoded in result code
        } else {
            throw new RuntimeException("class must be annotated with @BeanVersion: " + element.getType().getCanonicalName());
        }

        //save class model hash code
        builder.append(tab(2, "out.writeInt(" + CodeGenUtils.getClassModelHashCodeBasedOnClassFields(fields) +");\n"));

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
                .append(tab(3, "com.googlecode.icegem.serialization.codegen.MethodFrameCounter.exitFrame();\n"))
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