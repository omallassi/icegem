package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

import com.griddynamics.gemfire.serialization.AutoSerializable;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 909887678)
public class Bean {
    private MarkedParent markedParent;
    private NotMarkedParent notMarkedParent;

    public MarkedParent getMarkedParent() {
        return markedParent;
    }

    public void setMarkedParent(MarkedParent markedParent) {
        this.markedParent = markedParent;
    }

    public NotMarkedParent getNotMarkedParent() {
        return notMarkedParent;
    }

    public void setNotMarkedParent(NotMarkedParent notMarkedParent) {
        this.notMarkedParent = notMarkedParent;
    }
}
