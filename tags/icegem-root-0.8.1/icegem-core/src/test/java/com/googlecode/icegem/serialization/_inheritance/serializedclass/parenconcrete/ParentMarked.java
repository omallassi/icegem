package com.googlecode.icegem.serialization._inheritance.serializedclass.parenconcrete;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

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
