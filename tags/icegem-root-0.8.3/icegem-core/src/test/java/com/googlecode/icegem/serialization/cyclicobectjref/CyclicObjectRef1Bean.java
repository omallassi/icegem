package com.googlecode.icegem.serialization.cyclicobectjref;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

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
