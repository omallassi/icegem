package com.googlecode.icegem.serialization.versioning.beans.inheritance.v1;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 9872340)
@BeanVersion(1)
public class Son extends Father {
    private List<Long> brothers;

    public Son() {
    }

    public List<Long> getBrothers() {
        return brothers;
    }

    public void setBrothers(List<Long> brothers) {
        this.brothers = brothers;
    }
}
