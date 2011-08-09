package com.googlecode.icegem.serialization.codegen.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.icegem.serialization.codegen.XProperty;
import com.googlecode.icegem.serialization.codegen.impl.primitive.ToDataPrimitiveProcessor;
import com.googlecode.icegem.serialization.codegen.impl.primitivearray.ToDataFieldPrimitiveArrayProcessor;
import com.googlecode.icegem.serialization.codegen.impl.system.ToDataFieldCalendarProcessor;
import com.googlecode.icegem.serialization.codegen.impl.system.ToDataFieldConcreteEnumProcessor;
import com.googlecode.icegem.serialization.codegen.impl.system.ToDataFieldDateProcessor;
import com.googlecode.icegem.serialization.codegen.impl.system.ToDataFieldStringProcessor;
import com.googlecode.icegem.serialization.codegen.impl.wrapper.ToDataFieldWrapperProcessor;

/**
 * Generate code that dispatched by 'SOME TYPE' and
 * 1) get 'SOME TYPE' field value from bean
 * 2) write it to DataOutput
 * for method DataSerializer.toData(...)
 *
 * @author igolovach
 */

public class ToDataFieldProcessor {
    public Map<Class<?>, ToDataProcessor> map = new HashMap<Class<?>, ToDataProcessor>(); //todo: make private

    public ToDataFieldProcessor() {  //todo: what if different CLs?
        // primitive
        map.put(boolean.class, new ToDataPrimitiveProcessor("writeBoolean"));
        map.put(byte.class, new ToDataPrimitiveProcessor("writeByte"));
        map.put(char.class, new ToDataPrimitiveProcessor("writeChar"));
        map.put(short.class, new ToDataPrimitiveProcessor("writeShort"));
        map.put(int.class, new ToDataPrimitiveProcessor("writeInt"));
        map.put(long.class, new ToDataPrimitiveProcessor("writeLong"));
        map.put(float.class, new ToDataPrimitiveProcessor("writeFloat"));
        map.put(double.class, new ToDataPrimitiveProcessor("writeDouble"));
        // wrapper
        map.put(Boolean.class, new ToDataFieldWrapperProcessor("booleanValue", "writeBoolean"));
        map.put(Byte.class, new ToDataFieldWrapperProcessor("byteValue", "writeByte"));
        map.put(Character.class, new ToDataFieldWrapperProcessor("charValue", "writeChar"));
        map.put(Short.class, new ToDataFieldWrapperProcessor("shortValue", "writeShort"));
        map.put(Integer.class, new ToDataFieldWrapperProcessor("intValue", "writeInt"));
        map.put(Long.class, new ToDataFieldWrapperProcessor("longValue", "writeLong"));
        map.put(Float.class, new ToDataFieldWrapperProcessor("floatValue", "writeFloat"));
        map.put(Double.class, new ToDataFieldWrapperProcessor("doubleValue", "writeDouble"));
        // primitive[]
        map.put(boolean[].class, new ToDataFieldPrimitiveArrayProcessor("writeBooleanArray"));
        map.put(byte[].class, new ToDataFieldPrimitiveArrayProcessor("writeByteArray"));
        map.put(short[].class, new ToDataFieldPrimitiveArrayProcessor("writeShortArray"));
        map.put(char[].class, new ToDataFieldPrimitiveArrayProcessor("writeCharArray"));
        map.put(int[].class, new ToDataFieldPrimitiveArrayProcessor("writeIntArray"));
        map.put(long[].class, new ToDataFieldPrimitiveArrayProcessor("writeLongArray"));
        map.put(float[].class, new ToDataFieldPrimitiveArrayProcessor("writeFloatArray"));
        map.put(double[].class, new ToDataFieldPrimitiveArrayProcessor("writeDoubleArray"));
        // system
        map.put(String.class, new ToDataFieldStringProcessor());
        map.put(Date.class, new ToDataFieldDateProcessor());
        map.put(Calendar.class, new ToDataFieldCalendarProcessor());
        //todo: Locale: StringToLocaleConverter
    }

    public String process(XProperty field) {
        final Class<?> fieldClass = field.getType();

        // predefined
        if (map.get(fieldClass) != null) {
            return map.get(fieldClass).process(field);
        }

        // concrete enum class (not Enum)
        if (Enum.class.isAssignableFrom(fieldClass) && fieldClass != Enum.class) {
            return new ToDataFieldConcreteEnumProcessor().process(field);
        }

        //todo: what will be if field of type java.sql.Date/Time/Timestamp?
        return new ToDataFieldResolveClassByGemFireProcessor().process(field);
    }
}
