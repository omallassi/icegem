package com.griddynamics.gemfire.serialization.versioning.beans.beanv1;

import com.griddynamics.gemfire.serialization.AutoSerializable;
import com.griddynamics.gemfire.serialization.BeanVersion;

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
