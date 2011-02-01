package com.griddynamics.gemfire.serialization.perf.impl;

import com.griddynamics.gemfire.serialization.SerializedClass;

/**
 * @author igolovach
 */
@SerializedClass(dataSerializerID = 1)
public class FlatSlimPerfBean implements java.io.Serializable {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
