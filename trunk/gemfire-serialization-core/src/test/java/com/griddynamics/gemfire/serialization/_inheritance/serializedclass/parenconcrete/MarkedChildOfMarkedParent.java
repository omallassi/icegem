package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parenconcrete;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 17)
public class MarkedChildOfMarkedParent extends ParentMarked {
    private int childData;

    public int getChildData() {
        return childData;
    }

    public void setChildData(int childData) {
        this.childData = childData;
    }
}
