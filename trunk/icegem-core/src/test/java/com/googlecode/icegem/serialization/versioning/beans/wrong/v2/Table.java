package com.googlecode.icegem.serialization.versioning.beans.wrong.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 78461937)
@BeanVersion(1)
public class Table {
    private int type;

    public Table() {
    }

    public Table(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Table {" +
                "id=" + type +
                '}';
    }
}
