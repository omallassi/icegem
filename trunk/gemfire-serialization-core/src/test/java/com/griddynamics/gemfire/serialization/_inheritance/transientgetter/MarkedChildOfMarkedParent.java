package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

import com.griddynamics.gemfire.serialization.SerializedClass;
import com.griddynamics.gemfire.serialization.TransientGetter;

/**
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 5550)
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