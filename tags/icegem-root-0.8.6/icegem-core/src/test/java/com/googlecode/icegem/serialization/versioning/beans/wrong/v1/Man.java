package com.googlecode.icegem.serialization.versioning.beans.wrong.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 34578582)
@BeanVersion(1)
public class Man {
    private int id;

    public Man() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Man {" +
                "id=" + id +
                '}';
    }
}
