package com.griddynamics.gemfire.serialization._inheritance.serializedclass.pareninterface;

import com.griddynamics.gemfire.serialization.AutoSerializable;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 9543346)
public class MarkedChildOfNotMarkedParent implements ParentNotMarked {
    private int childData;

    public int getChildData() {
        return childData;
    }

    public void setChildData(int childData) {
        this.childData = childData;
    }
}
