package com.googlecode.icegem.serialization._inheritance.serializedclass.parentabstract;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 2323243)
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
