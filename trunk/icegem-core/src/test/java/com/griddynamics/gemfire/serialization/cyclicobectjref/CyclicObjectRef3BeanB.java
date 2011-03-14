package com.griddynamics.gemfire.serialization.cyclicobectjref;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 6)
@BeanVersion(1)
public class CyclicObjectRef3BeanB {
    private CyclicObjectRef3BeanC next;

    public CyclicObjectRef3BeanC getNext() {
        return next;
    }

    public void setNext(CyclicObjectRef3BeanC next) {
        this.next = next;
    }
}
