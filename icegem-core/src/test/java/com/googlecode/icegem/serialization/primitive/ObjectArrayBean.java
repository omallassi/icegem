package com.googlecode.icegem.serialization.primitive;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

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
