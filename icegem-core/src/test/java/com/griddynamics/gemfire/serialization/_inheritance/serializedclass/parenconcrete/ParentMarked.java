package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parenconcrete;

import com.griddynamics.gemfire.serialization.AutoSerializable;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 111222)
public class ParentMarked {
    private int parentData;

    public int getParentData() {
        return parentData;
    }

    public void setParentData(int parentData) {
        this.parentData = parentData;
    }
}
