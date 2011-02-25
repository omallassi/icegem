package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.TransientGetter;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 5550)
public class MarkedChildOfMarkedParent extends MarkedParent {
    
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