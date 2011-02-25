package com.griddynamics.gemfire.serialization.codegen.impl;

import com.griddynamics.gemfire.serialization.codegen.XField;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.*;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.*;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldCalendarProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldConcreteEnumProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldDateProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldStringProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate code that dispatched by 'SOME TYPE' and
 * 1) read 'SOME TYPE' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 *
 * @author igolovach
 */

public class FromDataFieldProcessor {
    private Map<Class<?>, FromDataProcessor> map = new HashMap<Class<?>, FromDataProcessor>();

    public FromDataFieldProcessor() { //todo: what if different CLs?
        // primitive
        map.put(boolean.class, new FromDataPrimitiveProcessor("readBoolean"));
        map.put(byte.class, new FromDataPrimitiveProcessor("readByte"));
        map.put(char.class, new FromDataPrimitiveProcessor("readChar"));
        map.put(short.class, new FromDataPrimitiveProcessor("readShort"));
        map.put(int.class, new FromDataPrimitiveProcessor("readInt"));
        map.put(long.class, new FromDataPrimitiveProcessor("readLong"));
        map.put(float.class, new FromDataPrimitiveProcessor("readFloat"));
        map.put(double.class, new FromDataPrimitiveProcessor("readDouble"));
        // wrapper
        map.put(Boolean.class, new FromDataFieldWrapperProcessor("Boolean", "readBoolean"));
        map.put(Byte.class, new FromDataFieldWrapperProcessor("Byte", "readByte"));
        map.put(Character.class, new FromDataFieldWrapperProcessor("Character", "readChar"));
        map.put(Short.class, new FromDataFieldWrapperProcessor("Short", "readShort"));
        map.put(Integer.class, new FromDataFieldWrapperProcessor("Integer", "readInt"));
        map.put(Long.class, new FromDataFieldWrapperProcessor("Long", "readLong"));
        map.put(Float.class, new FromDataFieldWrapperProcessor("Float", "readFloat"));
        map.put(Double.class, new FromDataFieldWrapperProcessor("Double", "readDouble"));
        // primitive[]
        map.put(boolean[].class, new FromDataFieldPrimitiveArrayProcessor("readBooleanArray"));
        map.put(byte[].class, new FromDataFieldPrimitiveArrayProcessor("readByteArray"));
        map.put(short[].class, new FromDataFieldPrimitiveArrayProcessor("readShortArray"));
        map.put(char[].class, new FromDataFieldPrimitiveArrayProcessor("readCharArray"));
        map.put(int[].class, new FromDataFieldPrimitiveArrayProcessor("readIntArray"));
        map.put(long[].class, new FromDataFieldPrimitiveArrayProcessor("readLongArray"));
        map.put(float[].class, new FromDataFieldPrimitiveArrayProcessor("readFloatArray"));
        map.put(double[].class, new FromDataFieldPrimitiveArrayProcessor("readDoubleArray"));
        // system
        map.put(String.class, new FromDataFieldStringProcessor());
        map.put(Date.class, new FromDataFieldDateProcessor());
        map.put(Calendar.class, new FromDataFieldCalendarProcessor());
    }

    public String process(XField field) {
        final Class<?> fieldClass = field.getType();

        // predefined
        if (map.get(fieldClass) != null) {
            return map.get(fieldClass).process(field);
        }

        // concrete enum class (not Enum)
        if (Enum.class.isAssignableFrom(fieldClass) && fieldClass != Enum.class) {
            return new FromDataFieldConcreteEnumProcessor().process(field);
        }

        return new FromDataFieldResolveClassByGemFireProcessor().process(field);
    }
}
