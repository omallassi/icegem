package com.griddynamics.gemfire.serialization.cyclicobectjref;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 4)
@BeanVersion(1)
public class CyclicObjectRef1Bean {
    private CyclicObjectRef1Bean next;

    public CyclicObjectRef1Bean getNext() {
        return next;
    }

    public void setNext(CyclicObjectRef1Bean next) {
        this.next = next;
    }
}
