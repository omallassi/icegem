package com.googlecode.icegem.serialization.versioning.beans.inheritance.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 9872340)
@BeanVersion(2)
public class Son extends Father {
    private List<Long> brothers;
    private List<Long> sisters;

    public Son() {
    }

    public List<Long> getBrothers() {
        return brothers;
    }

    public void setBrothers(List<Long> brothers) {
        this.brothers = brothers;
    }

    @SinceVersion(2)
    public List<Long> getSisters() {
        return sisters;
    }

    public void setSisters(List<Long> sisters) {
        this.sisters = sisters;
    }
}
