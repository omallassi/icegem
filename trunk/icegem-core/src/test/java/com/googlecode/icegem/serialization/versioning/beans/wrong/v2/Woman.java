package com.googlecode.icegem.serialization.versioning.beans.wrong.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 12904587)
@BeanVersion(2)
public class Woman {
    private int id;

    public Woman() {
    }

    @SinceVersion(2)
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
