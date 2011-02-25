package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 5552)
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