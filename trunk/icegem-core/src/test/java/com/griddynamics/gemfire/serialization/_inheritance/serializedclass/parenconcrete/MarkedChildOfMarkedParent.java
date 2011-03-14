package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parenconcrete;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 17)
@BeanVersion(1)
public class MarkedChildOfMarkedParent extends ParentMarked {
    private int childData;

    public int getChildData() {
        return childData;
    }

    public void setChildData(int childData) {
        this.childData = childData;
    }
}
