package com.griddynamics.gemfire.serialization.codegen;

import com.griddynamics.gemfire.serialization.BeanVersion;
import com.griddynamics.gemfire.serialization.codegen.impl.ToDataFieldProcessor;

import java.util.List;

import static com.griddynamics.gemfire.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer method toData()
 *
 * @author igolovach
 */

public class MethodToDataProcessor {

    public String process(XClass element) {
        if (Enum.class.isAssignableFrom(element.getType())) {
            if (element.getType() == Enum.class) {
                throw new InternalError("Never here!"); //todo: correct ex? more info?
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
                .append(tab("// check arg is of correct type\n"))
                .append(tab("if (obj.getClass() != " + className + ".class) {return false;}\n"))
                .append(tab("// increment thread-local method-frame counter\n"))
                // todo: not call if fields - not beans! for example - primitives
                // todo: analyze - if exception - we clean counter?
                .append(tab("com.griddynamics.gemfire.serialization.codegen.MethodFrameCounter.enterFrame();\n")) //todo: analize exist MethodFrameCounter.class in runtime
                .append(tab("// convert to concrete type\n"))
                .append(tab(className)).append(" concrete = (").append(className).append(") obj;\n");

        builder.append("\n");

        //save bean value
        if (element.getType().getAnnotation(BeanVersion.class) != null) {
            builder.append(tab("out.writeInt(" + element.getType().getAnnotation(BeanVersion.class).value()+");"));  //todo: value is harcoded in result code
        } else {
            throw new RuntimeException("class must be annotated with @BeanVersion: " + element.getType().getCanonicalName());
        }

        for (XField field : fields) {
            builder.append("\n");
            builder.append(tab("// this." + field.getName() + " -> byte[]\n")); //todo: can be name collision between parent/child fields
            builder.append(tab(new ToDataFieldProcessor().process(field)));
        }
        builder.append("\n");

        builder.append(tab("// decrement thread-local method-frame counter\n"));
        builder.append(tab("com.griddynamics.gemfire.serialization.codegen.MethodFrameCounter.exitFrame();\n"));

        // method end
        builder.append(tab("return true;\n"))
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
