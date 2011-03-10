package com.griddynamics.gemfire.serialization.cyclicobectjref;

import com.griddynamics.gemfire.serialization.AutoSerializable;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 5)
public class CyclicObjectRef3BeanA {
    private CyclicObjectRef3BeanB next;

    public CyclicObjectRef3BeanB getNext() {
        return next;
    }

    public void setNext(CyclicObjectRef3BeanB next) {
        this.next = next;
    }
}