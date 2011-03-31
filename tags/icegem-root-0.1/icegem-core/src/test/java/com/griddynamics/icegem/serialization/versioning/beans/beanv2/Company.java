package com.griddynamics.icegem.serialization.versioning.beans.beanv2;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;
import com.griddynamics.icegem.serialization.FieldVersion;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 1412)
@BeanVersion(2)
public class Company {
    private int id;
    @FieldVersion(since = 2)
    private String name;

    public Company() {
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
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
