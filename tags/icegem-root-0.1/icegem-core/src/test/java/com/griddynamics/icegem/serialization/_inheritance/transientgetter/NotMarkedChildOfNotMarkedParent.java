package com.griddynamics.icegem.serialization._inheritance.transientgetter;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 5553)
@BeanVersion(1)
public class NotMarkedChildOfNotMarkedParent extends NotMarkedParent {

    @Override
    public int getData() {
        return super.getData();
    }

    @Override
    public void setData(int data) {
        super.setData(data);
    }
}