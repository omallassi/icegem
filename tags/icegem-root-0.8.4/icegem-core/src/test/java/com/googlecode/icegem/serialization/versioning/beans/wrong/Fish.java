package com.googlecode.icegem.serialization.versioning.beans.wrong;

import com.googlecode.icegem.serialization.AutoSerializable;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 234235)
public class Fish {
    private String name;

    public Fish() {
    }

    public Fish(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Fish {" +
                "name=" + name +
                '}';
    }
}
