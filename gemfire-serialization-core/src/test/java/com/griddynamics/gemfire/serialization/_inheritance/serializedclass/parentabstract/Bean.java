package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parentabstract;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 8976346)
public class Bean {
    private ParentNotMarked parentNotMarked;

    public ParentNotMarked getParentNotMarked() {
        return parentNotMarked;
    }

    public void setParentNotMarked(ParentNotMarked parentNotMarked) {
        this.parentNotMarked = parentNotMarked;
    }
}
