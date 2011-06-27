package com.googlecode.icegem.serialization.versioning.beans.versionhistory.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka id
 */
@AutoSerializable(dataSerializerID = 34814375, versionHistoryLength = 1)
@BeanVersion(3)
public class Keyboard {
    private String name;

    public Keyboard() {
    }

    public Keyboard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Keyboard {" +
                ", name='" + name + '\'' +
                '}';
    }
}
