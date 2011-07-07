package com.googlecode.icegem.serialization._inheritance.transientgetter;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.Transient;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 55433234)
@BeanVersion(1)
public class MarkedChildOfNotMarkedParent extends NotMarkedParent {

    @Override
    @Transient
    public int getData() {
        return super.getData();
    }

    @Override
    public void setData(int data) {
        super.setData(data);
    }
}