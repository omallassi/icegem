package com.griddynamics.gemfire.serialization._enum;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 1232325233)
@BeanVersion(1)
public enum ExtendedFinalEnumBean {
    X("aaa"), Y("bbb"), Z("ccc");

    private final String finalName;

    private ExtendedFinalEnumBean(String finalName) {
        this.finalName = finalName;
    }

    public String getFinalName() {
        return finalName;
    }
}
