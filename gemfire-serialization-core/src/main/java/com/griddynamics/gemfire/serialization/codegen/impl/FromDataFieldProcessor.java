package com.griddynamics.gemfire.serialization.codegen.impl;

import com.griddynamics.gemfire.serialization.codegen.XField;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPBoolProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPByteProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPCharProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPDoubleProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPFloatProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPIntProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPLongProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitive.FromDataFieldPShortProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldBooleanArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldByteArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldCharArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldDoubleArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldFloatArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldIntArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldLongArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.primitivearray.FromDataFieldShortArrayProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldCalendarProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldConcreteEnumProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldDateProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.system.FromDataFieldStringProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWBooleanProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWByteProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWCharacterProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWDoubleProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWFloatProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWIntegerProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWLongProcessor;
import com.griddynamics.gemfire.serialization.codegen.impl.wrapper.FromDataFieldWShortProcessor;

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
        map.put(boolean.class, new FromDataFieldPBoolProcessor());
        map.put(byte.class, new FromDataFieldPByteProcessor());
        map.put(char.class, new FromDataFieldPCharProcessor());
        map.put(short.class, new FromDataFieldPShortProcessor());
        map.put(int.class, new FromDataFieldPIntProcessor());
        map.put(long.class, new FromDataFieldPLongProcessor());
        map.put(float.class, new FromDataFieldPFloatProcessor());
        map.put(double.class, new FromDataFieldPDoubleProcessor());
        // wrapper
        map.put(Boolean.class, new FromDataFieldWBooleanProcessor());
        map.put(Byte.class, new FromDataFieldWByteProcessor());
        map.put(Character.class, new FromDataFieldWCharacterProcessor());
        map.put(Short.class, new FromDataFieldWShortProcessor());
        map.put(Integer.class, new FromDataFieldWIntegerProcessor());
        map.put(Long.class, new FromDataFieldWLongProcessor());
        map.put(Float.class, new FromDataFieldWFloatProcessor());
        map.put(Double.class, new FromDataFieldWDoubleProcessor());
        // primitive[]
        map.put(boolean[].class, new FromDataFieldBooleanArrayProcessor());
        map.put(byte[].class, new FromDataFieldByteArrayProcessor());
        map.put(short[].class, new FromDataFieldShortArrayProcessor());
        map.put(char[].class, new FromDataFieldCharArrayProcessor());
        map.put(int[].class, new FromDataFieldIntArrayProcessor());
        map.put(long[].class, new FromDataFieldLongArrayProcessor());
        map.put(float[].class, new FromDataFieldFloatArrayProcessor());
        map.put(double[].class, new FromDataFieldDoubleArrayProcessor());
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
