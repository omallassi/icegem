package com.googlecode.icegem.serialization.versioning.beans.modified.beanv1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 7654321)
@BeanVersion(1)
public class Person {
    private int id;

    public Person() {
    }

    public Person(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Person {" +
                "id=" + id +
                '}';
    }
}
