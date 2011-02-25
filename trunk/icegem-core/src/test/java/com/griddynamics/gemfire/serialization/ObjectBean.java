package com.griddynamics.gemfire.serialization;

/**
 * @author igolovach
 */

@SerializedClass(dataSerializerID = 10)
public class ObjectBean {
    private Object obj;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
