package com.googlecode.icegem.serialization.versioning.beans.inheritance.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

import java.util.ArrayList;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 9872340)
@BeanVersion(1)
public class Son extends Father {
    private ArrayList<Long> brothers;

    public Son() {
    }

    public ArrayList<Long> getBrothers() {
        return brothers;
    }

    public void setBrothers(ArrayList<Long> brothers) {
        this.brothers = brothers;
    }
}
