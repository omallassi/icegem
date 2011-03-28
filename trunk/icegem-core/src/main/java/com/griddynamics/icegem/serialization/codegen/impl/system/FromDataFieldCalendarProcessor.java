package com.griddynamics.icegem.serialization.codegen.impl.system;

import com.griddynamics.icegem.serialization.codegen.XField;
import com.griddynamics.icegem.serialization.codegen.impl.FromDataProcessor;

import java.util.GregorianCalendar;

import static com.griddynamics.icegem.serialization.codegen.CodeGenUtils.firstLetterToUpperCase;
import static com.griddynamics.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Logic copied from Hessian: com.caucho.hessian.io.CalendarHandle
 *
 * @author igolovach
 */

//todo: if gregorian:
// todo: 1) new GregCalendar();
// todo: 2) let name be 'G'

// todo: what about other calendars (Yulian)?
public class FromDataFieldCalendarProcessor implements FromDataProcessor {
    public String process(XField field) {
        String fieldName = field.getName();
        return "if (in.readByte() != 0) {\n" +
                tab("String calendarClassName = in.readUTF();\n") +
                tab("java.util.Calendar newCalendar;\n") +

                tab("if (calendarClassName.equals(\"" + GregorianCalendar.class.getName() + "\")) {\n") +
                tab(tab("newCalendar = new java.util.GregorianCalendar();\n")) +
                tab("} else {\n") +
                tab(tab("try {\n")) +
                tab(tab(tab("Class calendarClass = Class.forName(in.readUTF());\n"))) +
                tab(tab(tab("newCalendar = (java.util.Calendar)calendarClass.newInstance();\n"))) +
                tab(tab("} catch (Throwable t) {\n")) +
                tab(tab(tab("throw new RuntimeException(\"Never here!\", t);\n"))) +
                tab(tab("}\n")) +
                tab("}\n") +

                tab("long timiInMillis = in.readLong();\n") +
                tab("newCalendar.setTimeInMillis(timiInMillis);\n") +
                tab("result.set" + firstLetterToUpperCase(fieldName) + "(newCalendar);\n") +
                "}\n";
    }
}
