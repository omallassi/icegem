package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

/**
 * @author igolovach
 */

public class NotMarkedParent {
    private int data;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
