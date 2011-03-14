package com.griddynamics.gemfire.serialization.cyclicobectjref;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 5)
@BeanVersion(1)
public class CyclicObjectRef3BeanA {
    private CyclicObjectRef3BeanB next;

    public CyclicObjectRef3BeanB getNext() {
        return next;
    }

    public void setNext(CyclicObjectRef3BeanB next) {
        this.next = next;
    }
}
