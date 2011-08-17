package com.googlecode.icegem.serialization.versioning.beans.wrong;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 2345856)
@BeanVersion(1)
public class Bird {
    private String name;

    public Bird() {
    }

    public Bird(String name) {
        this.name = name;
    }

    @SinceVersion(-1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Bird {" +
                "name=" + name +
                '}';
    }
}
