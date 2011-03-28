package com.griddynamics.icegem.serialization.example.bean;

import com.griddynamics.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */

@BeanVersion(1)
public class ParentParentCompany {

    private int parentParentData;

    public int getParentParentData() {
        return parentParentData;
    }

    public void setParentParentData(int someParentData) {
        this.parentParentData = someParentData;
    }
}
