package com.googlecode.icegem.serialization.versioning.beans.wrong.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 12904587)
@BeanVersion(1)
public class Woman {
    private int id;

    public Woman() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Woman {" +
                "id=" + id +
                '}';
    }
}
