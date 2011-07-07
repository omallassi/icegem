package com.googlecode.icegem.serialization.versioning.beans.manyVersions.v3;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 1239870284)
@BeanVersion(3)
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

    @SinceVersion(2)
    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    @SinceVersion(3)
    public boolean isSedan() {
        return sedan;
    }

    public void setSedan(boolean sedan) {
        this.sedan = sedan;
    }

    @Override
    public String toString() {
        return "Car{" +
                "model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", seatCount=" + seatCount +
                ", is sedan='" + sedan + '\'' +
                '}';
    }

    private String model;
    private String version;
    private int seatCount = 4;//default value
    private boolean sedan = true;//default value
}
