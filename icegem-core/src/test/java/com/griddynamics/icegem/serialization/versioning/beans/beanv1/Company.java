package com.griddynamics.icegem.serialization.versioning.beans.beanv1;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 1412)
@BeanVersion(1)
public class Company {
    private int id;

    public Company() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "Company{" +
                '}';
    }
}
