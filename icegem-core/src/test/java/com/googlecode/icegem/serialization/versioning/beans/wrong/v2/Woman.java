package com.googlecode.icegem.serialization.versioning.beans.wrong.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.FieldVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 12904587)
@BeanVersion(2)
public class Woman {
    @FieldVersion(since = 2)
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
