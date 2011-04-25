package com.googlecode.icegem.serialization.versioning.inheritance.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.FieldVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 9872340)
@BeanVersion(2)
public class Son extends Father {
    private List<Long> brothers;
    @FieldVersion(since = 2)
    private ArrayList<Long> sisters;

    public Son() {
    }

    public List<Long> getBrothers() {
        return brothers;
    }

    public void setBrothers(List<Long> brothers) {
        this.brothers = brothers;
    }

    public ArrayList<Long> getSisters() {
        return sisters;
    }

    public void setSisters(ArrayList<Long> sisters) {
        this.sisters = sisters;
    }
}
