package com.griddynamics.gemfire.serialization.codegen.impl;

import com.griddynamics.gemfire.serialization.codegen.XField;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPBoolProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPByteProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPCharProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPDoubleProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPFloatProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPIntProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPLongProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.ToDataFieldPShortProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldBooleanArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldByteArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldCharArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldDoubleArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldFloatArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldIntArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldLongArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.ToDataFieldShortArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.ToDataFieldCalendarProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.ToDataFieldConcreteEnumProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.ToDataFieldDateProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.ToDataFieldStringProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWBooleanProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWByteProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWCharacterProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWDoubleProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWFloatProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWIntegerProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWLongProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.ToDataFieldWShortProcessor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        map.put(boolean.class, new ToDataFieldPBoolProcessor());
        map.put(byte.class, new ToDataFieldPByteProcessor());
        map.put(char.class, new ToDataFieldPCharProcessor());
        map.put(short.class, new ToDataFieldPShortProcessor());
        map.put(int.class, new ToDataFieldPIntProcessor());
        map.put(long.class, new ToDataFieldPLongProcessor());
        map.put(float.class, new ToDataFieldPFloatProcessor());
        map.put(double.class, new ToDataFieldPDoubleProcessor());
        // wrapper
        map.put(Boolean.class, new ToDataFieldWBooleanProcessor());
        map.put(Byte.class, new ToDataFieldWByteProcessor());
        map.put(Character.class, new ToDataFieldWCharacterProcessor());
        map.put(Short.class, new ToDataFieldWShortProcessor());
        map.put(Integer.class, new ToDataFieldWIntegerProcessor());
        map.put(Long.class, new ToDataFieldWLongProcessor());
        map.put(Float.class, new ToDataFieldWFloatProcessor());
        map.put(Double.class, new ToDataFieldWDoubleProcessor());
        // primitive[]
        map.put(boolean[].class, new ToDataFieldBooleanArrayProcessor());
        map.put(byte[].class, new ToDataFieldByteArrayProcessor());
        map.put(short[].class, new ToDataFieldShortArrayProcessor());
        map.put(char[].class, new ToDataFieldCharArrayProcessor());
        map.put(int[].class, new ToDataFieldIntArrayProcessor());
        map.put(long[].class, new ToDataFieldLongArrayProcessor());
        map.put(float[].class, new ToDataFieldFloatArrayProcessor());
        map.put(double[].class, new ToDataFieldDoubleArrayProcessor());
        // system
        map.put(String.class, new ToDataFieldStringProcessor());
        map.put(Date.class, new ToDataFieldDateProcessor());
        map.put(Calendar.class, new ToDataFieldCalendarProcessor());
        //todo: Locale: StringToLocaleConverter
    }

    public String process(XField field) {
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

