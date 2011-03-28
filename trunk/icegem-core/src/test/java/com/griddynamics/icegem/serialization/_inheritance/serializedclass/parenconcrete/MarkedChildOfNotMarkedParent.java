package com.griddynamics.icegem.serialization._inheritance.serializedclass.parenconcrete;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 8957)
@BeanVersion(1)
public class MarkedChildOfNotMarkedParent extends ParentNotMarked {
    private int childData;

    public int getChildData() {
        return childData;
    }

    public void setChildData(int childData) {
        this.childData = childData;
    }
}
