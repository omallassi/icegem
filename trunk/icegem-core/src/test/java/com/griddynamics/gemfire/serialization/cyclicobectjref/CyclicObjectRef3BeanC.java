package com.griddynamics.gemfire.serialization.cyclicobectjref;

import com.griddynamics.gemfire.serialization.AutoSerializable;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 7)
public class CyclicObjectRef3BeanC {
    private CyclicObjectRef3BeanA next;

    public CyclicObjectRef3BeanA getNext() {
        return next;
    }

    public void setNext(CyclicObjectRef3BeanA next) {
        this.next = next;
    }
}
