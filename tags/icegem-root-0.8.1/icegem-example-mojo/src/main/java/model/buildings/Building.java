package model.buildings;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

import java.io.Serializable;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 4312)
@BeanVersion(1)
public class Building {
    private String name;

    public Building() {
    }

    public Building(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}