package com.googlecode.icegem.serialization._inheritance.transientgetter;

import com.googlecode.icegem.serialization.Transient;

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
