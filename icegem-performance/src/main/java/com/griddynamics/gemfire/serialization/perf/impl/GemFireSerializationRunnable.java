package com.griddynamics.gemfire.serialization.perf.impl;

import com.gemstone.gemfire.DataSerializer;
import com.griddynamics.gemfire.serialization.codegen.DataSerializerGenerator;
import javassist.CannotCompileException;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author igolovach
 */
public class GemFireSerializationRunnable implements ExceptionalRunnable {
    public final static Map<Class<?>, DataSerializer> MAP = new HashMap<Class<?>, DataSerializer>(); //todo: bad architecture

    private final DataSerializer ser;
//    private final DataOutput out = new NullDataOutput(); //todo: or in such way?
    private final DataOutput out = new DataOutputStream(new NullOutputStream());

    private final Object bean;

    public GemFireSerializationRunnable(final Object bean) throws InvalidClassException, CannotCompileException, IllegalAccessException, InstantiationException {
        this.bean = bean;
        List<Class<?>> beanClassList = new ArrayList<Class<?>>() {{
            add(bean.getClass());
        }};
        List<Class<?>> serClassList = DataSerializerGenerator.generateDataSerializerClasses(Thread.currentThread().getContextClassLoader(), beanClassList);
        Class<?> serClass = serClassList.get(0);
        this.ser = (DataSerializer) serClass.newInstance();
        MAP.put(bean.getClass(), ser);
    }

    public void run() throws Throwable {
        ser.toData(bean, out);
    }
}
