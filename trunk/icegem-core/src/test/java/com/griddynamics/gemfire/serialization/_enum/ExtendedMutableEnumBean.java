package com.griddynamics.gemfire.serialization._enum;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * Strange but valid class.
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 989798231)
@BeanVersion(1)
public enum ExtendedMutableEnumBean {
    K("aaa"), L("bbb"), M("ccc");

    private String name;

    ExtendedMutableEnumBean(String name) {
        this.name = name;
    }

    public String getMutableName() {
        return name;
    }

    public void setMutableName(String name) {
        this.name = name;
    }
}
