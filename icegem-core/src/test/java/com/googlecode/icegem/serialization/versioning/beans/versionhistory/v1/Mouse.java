package com.googlecode.icegem.serialization.versioning.beans.versionhistory.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 6491582, versionHistoryLength = 1)
@BeanVersion(2)
public class Mouse {
    private int id;

    public Mouse() {
    }

    public Mouse(int id) {
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
        return "Mouse {" +
                "id=" + id +
                '}';
    }
}
