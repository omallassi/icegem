package com.griddynamics.gemfire.serialization._inheritance.serializedclass.pareninterface;

import com.griddynamics.gemfire.serialization.AutoSerializable;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 96378)
public class Bean {
    private ParentNotMarked parentNotMarked;

    public ParentNotMarked getParentNotMarked() {
        return parentNotMarked;
    }

    public void setParentNotMarked(ParentNotMarked parentNotMarked) {
        this.parentNotMarked = parentNotMarked;
    }
}
