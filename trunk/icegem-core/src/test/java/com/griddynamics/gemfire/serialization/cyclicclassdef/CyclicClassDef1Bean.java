package com.griddynamics.gemfire.serialization.cyclicclassdef;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * Class with field of the same type.
 * 1-length class-def cycle.
 *
 * @author igolovach
 */

@SerializedClass(dataSerializerID = 1)
public class CyclicClassDef1Bean {
    private int data;
    private CyclicClassDef1Bean next;

    public CyclicClassDef1Bean() {
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public CyclicClassDef1Bean getNext() {
        return next;
    }

    public void setNext(CyclicClassDef1Bean next) {
        this.next = next;
    }
}
