package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parenconcrete;

import com.griddynamics.gemfire.serialization.AutoSerializable;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 86574685)
public class Bean {
    private ParentMarked parentMarked;
    private ParentNotMarked parentNotMarked;

    public ParentMarked getParentMarked() {
        return parentMarked;
    }

    public void setParentMarked(ParentMarked parentMarked) {
        this.parentMarked = parentMarked;
    }

    public ParentNotMarked getParentNotmarked() {
        return parentNotMarked;
    }

    public void setParentNotmarked(ParentNotMarked parentNotMarked) {
        this.parentNotMarked = parentNotMarked;
    }
}
