package com.griddynamics.gemfire.serialization.cyclicclassdef;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * Class with field of the other type.
 * 1-length class-def cycle.
 *
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 3)
public class CyclicClassDef2BeanB {
    private int data;
    private CyclicClassDef2BeanA next;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public CyclicClassDef2BeanA getNext() {
        return next;
    }

    public void setNext(CyclicClassDef2BeanA next) {
        this.next = next;
    }
}
