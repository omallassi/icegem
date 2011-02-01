package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parentabstract;

/**
 * @author igolovach
 */
public abstract class ParentNotMarked {
    private int parentData;

    public int getParentData() {
        return parentData;
    }

    public void setParentData(int parentData) {
        this.parentData = parentData;
    }
}
