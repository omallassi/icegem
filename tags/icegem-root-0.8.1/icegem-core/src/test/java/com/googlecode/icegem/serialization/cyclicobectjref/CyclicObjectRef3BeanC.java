package com.googlecode.icegem.serialization.cyclicobectjref;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 7)
@BeanVersion(1)
public class CyclicObjectRef3BeanC {
    private CyclicObjectRef3BeanA next;

    public CyclicObjectRef3BeanA getNext() {
        return next;
    }

    public void setNext(CyclicObjectRef3BeanA next) {
        this.next = next;
    }
}
