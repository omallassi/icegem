package com.griddynamics.gemfire.serialization.cyclicclassdef;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * Class with field of the other type.
 * 2-length class-def cycle.
 *
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 2)
public class CyclicClassDef2BeanA {
    private int data;
    private CyclicClassDef2BeanB next;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public CyclicClassDef2BeanB getNext() {
        return next;
    }

    public void setNext(CyclicClassDef2BeanB next) {
        this.next = next;
    }
}
