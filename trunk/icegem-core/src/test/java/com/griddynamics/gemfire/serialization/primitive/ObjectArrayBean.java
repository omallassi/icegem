package com.griddynamics.gemfire.serialization.primitive;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 9)
@BeanVersion(1)
public class ObjectArrayBean {
    private Object[] objArr;

    public Object[] getObjArr() {
        return objArr;
    }

    public void setObjArr(Object[] objArr) {
        this.objArr = objArr;
    }
}
