package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parentabstract;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 2323243)
public class MarkedChildOfNotMarkedParent extends ParentNotMarked {
    private int childData;

    public int getChildData() {
        return childData;
    }

    public void setChildData(int childData) {
        this.childData = childData;
    }
}
