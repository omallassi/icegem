package com.griddynamics.icegem.serialization._inheritance.transientgetter;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;
import com.griddynamics.icegem.serialization.Transient;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 5550)
@BeanVersion(1)
public class MarkedChildOfMarkedParent extends MarkedParent {
    
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