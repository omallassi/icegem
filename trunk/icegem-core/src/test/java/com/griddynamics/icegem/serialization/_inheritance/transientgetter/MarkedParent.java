package com.griddynamics.icegem.serialization._inheritance.transientgetter;

import com.griddynamics.icegem.serialization.Transient;

/**
 * @author igolovach
 */

public class MarkedParent {
    private int data;

    @Transient
    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
