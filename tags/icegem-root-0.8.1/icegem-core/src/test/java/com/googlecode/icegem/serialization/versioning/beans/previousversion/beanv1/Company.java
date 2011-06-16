package com.googlecode.icegem.serialization.versioning.beans.previousversion.beanv1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

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
