package com.googlecode.icegem.serialization._inheritance.transientgetter;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 5552)
@BeanVersion(1)
public class NotMarkedChildOfMarkedParent extends MarkedParent {

    @Override
    public int getData() {
        return super.getData();
    }

    @Override
    public void setData(int data) {
        super.setData(data);
    }
}