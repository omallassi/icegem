package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;
import com.griddynamics.gemfire.serialization.TransientGetter;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 55433234)
@BeanVersion(1)
public class MarkedChildOfNotMarkedParent extends NotMarkedParent {

    @Override
    @TransientGetter
    public int getData() {
        return super.getData();
    }

    @Override
    public void setData(int data) {
        super.setData(data);
    }
}