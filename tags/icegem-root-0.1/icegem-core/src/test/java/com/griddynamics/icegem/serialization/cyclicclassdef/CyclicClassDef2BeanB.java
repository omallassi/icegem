package com.griddynamics.icegem.serialization.cyclicclassdef;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

/**
 * Class with field of the other type.
 * 1-length class-def cycle.
 *
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 3)
@BeanVersion(1)
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
