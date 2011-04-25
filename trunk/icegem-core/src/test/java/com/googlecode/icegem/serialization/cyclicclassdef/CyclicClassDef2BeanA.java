package com.googlecode.icegem.serialization.cyclicclassdef;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * Class with field of the other type.
 * 2-length class-def cycle.
 *
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 2)
@BeanVersion(1)
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
