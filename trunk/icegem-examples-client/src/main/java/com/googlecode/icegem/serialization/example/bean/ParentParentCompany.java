package com.googlecode.icegem.serialization.example.bean;

import com.googlecode.icegem.serialization.BeanVersion;

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
