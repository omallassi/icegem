package com.griddynamics.icegem.serialization.versioning.manyVersions.v2;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;
import com.griddynamics.icegem.serialization.FieldVersion;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 1239870284)
@BeanVersion(2)
public class Car {

    public Car() {
    }

    public Car(String model, String version) {
        this.model = model;
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    @Override
    public String toString() {
        return "Car{" +
                "model='" + model + '\'' +
                ", value='" + version + '\'' +
                ", seatCount=" + seatCount +
                '}';
    }

    private String model;
    private String version;
    @FieldVersion(since = 2)
    private int seatCount;
}

