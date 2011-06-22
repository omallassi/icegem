package itest.com.googlecode.icegem.serialization.model;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.FieldVersion;

/**
 * @author Andrey Stepanov aka standy
 */
@AutoSerializable(dataSerializerID = 435612)
@BeanVersion(2)
public class Laptop {
    private int id;
    @FieldVersion(since = 2)
    private String name;

    public Laptop() {
    }

    public Laptop(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Laptop {" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

