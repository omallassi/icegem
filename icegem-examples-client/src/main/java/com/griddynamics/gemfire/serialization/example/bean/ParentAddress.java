package com.griddynamics.gemfire.serialization.example.bean;

import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */

@BeanVersion(1)
public class ParentAddress {

    private int parentData;

    public int getParentData() {
        return parentData;
    }

    public void setParentData(int someParentData) {
        this.parentData = someParentData;
    }
}
