package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

import com.griddynamics.gemfire.serialization.TransientGetter;

/**
 * @author igolovach
 */

public class MarkedParent {
    private int data;

    @TransientGetter
    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
