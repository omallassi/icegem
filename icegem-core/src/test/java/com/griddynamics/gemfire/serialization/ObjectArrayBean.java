package com.griddynamics.gemfire.serialization;

/**
 * @author igolovach
 */

@SerializedClass(dataSerializerID = 9)
public class ObjectArrayBean {
    private Object[] objArr;

    public Object[] getObjArr() {
        return objArr;
    }

    public void setObjArr(Object[] objArr) {
        this.objArr = objArr;
    }
}
