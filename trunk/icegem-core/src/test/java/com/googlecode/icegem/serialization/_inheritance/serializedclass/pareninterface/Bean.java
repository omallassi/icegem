package com.googlecode.icegem.serialization._inheritance.serializedclass.pareninterface;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 96378)
@BeanVersion(1)
public class Bean {
    private ParentNotMarked parentNotMarked;

    public ParentNotMarked getParentNotMarked() {
        return parentNotMarked;
    }

    public void setParentNotMarked(ParentNotMarked parentNotMarked) {
        this.parentNotMarked = parentNotMarked;
    }
}
