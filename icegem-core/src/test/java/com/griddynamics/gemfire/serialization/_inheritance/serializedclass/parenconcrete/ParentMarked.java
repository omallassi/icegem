package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parenconcrete;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 111222)
@BeanVersion(1)
public class ParentMarked {
    private int parentData;

    public int getParentData() {
        return parentData;
    }

    public void setParentData(int parentData) {
        this.parentData = parentData;
    }
}
