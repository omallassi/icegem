package com.griddynamics.icegem.serialization._inheritance.serializedclass.parenconcrete;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 86574685)
@BeanVersion(1)
public class Bean {
    private ParentMarked parentMarked;
    private ParentNotMarked parentNotMarked;

    public ParentMarked getParentMarked() {
        return parentMarked;
    }

    public void setParentMarked(ParentMarked parentMarked) {
        this.parentMarked = parentMarked;
    }

    public ParentNotMarked getParentNotMarked() {
        return parentNotMarked;
    }

    public void setParentNotMarked(ParentNotMarked parentNotMarked) {
        this.parentNotMarked = parentNotMarked;
    }
}
