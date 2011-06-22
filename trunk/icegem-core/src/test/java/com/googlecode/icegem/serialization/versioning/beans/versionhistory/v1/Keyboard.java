package com.googlecode.icegem.serialization.versioning.beans.versionhistory.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 34814375, versionHistoryLength = 1)
@BeanVersion(1)
public class Keyboard {
    private int id;

    public Keyboard() {
    }

    public Keyboard(int id) {
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
        return "Keyboard {" +
                "id=" + id +
                '}';
    }
}
