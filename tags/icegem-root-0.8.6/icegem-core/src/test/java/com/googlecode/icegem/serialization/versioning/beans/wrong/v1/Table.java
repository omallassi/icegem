package com.googlecode.icegem.serialization.versioning.beans.wrong.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 78461937)
@BeanVersion(1)
public class Table {
    private String type;

    public Table() {
    }

    public Table(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Table {" +
                "id=" + type +
                '}';
    }
}
