package com.griddynamics.gemfire.serialization.example.bean;

/**
 * @author igolovach
 */

public class ParentCompany extends ParentParentCompany {

    private int parentData;

    public int getParentData() {
        return parentData;
    }

    public void setParentData(int someParentData) {
        this.parentData = someParentData;
    }
}