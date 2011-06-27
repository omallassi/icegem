package com.googlecode.icegem.serialization.versioning.beans.versionhistory.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.FieldVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 6491582, versionHistoryLength = 2)
@BeanVersion(3)
public class Mouse {
    private int id2;
    @FieldVersion(since = 3)
    private String name;

    public Mouse() {
    }

    public Mouse(int id2) {
        this.id2 = id2;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Mouse {" +
                "id2=" + id2 +
                "name=" + name +
                '}';
    }
}
