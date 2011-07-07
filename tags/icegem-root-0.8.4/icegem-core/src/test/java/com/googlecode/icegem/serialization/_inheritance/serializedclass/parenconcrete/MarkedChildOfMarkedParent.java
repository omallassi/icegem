package com.googlecode.icegem.serialization._inheritance.serializedclass.parenconcrete;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

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
