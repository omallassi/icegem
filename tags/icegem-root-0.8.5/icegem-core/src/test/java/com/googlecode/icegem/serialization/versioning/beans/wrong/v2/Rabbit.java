package com.googlecode.icegem.serialization.versioning.beans.wrong.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 9856123)
@BeanVersion(2)
public class Rabbit {
    private int id;
    private String name;

    public Rabbit() {
    }

    public Rabbit(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    @Override
    public String toString() {
        return "Rabbit {" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
