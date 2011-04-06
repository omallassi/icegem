package com.googlecode.icegem.serialization.versioning.inheritance.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 8763)
@BeanVersion(1)
public class Father {
    private String name;
    private int age;

    public Father() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
