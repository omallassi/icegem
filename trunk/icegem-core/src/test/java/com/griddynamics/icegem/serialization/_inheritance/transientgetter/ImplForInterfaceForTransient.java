package com.griddynamics.icegem.serialization._inheritance.transientgetter;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;
import com.griddynamics.icegem.serialization.Transient;

import java.lang.reflect.Method;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 12342342)
@BeanVersion(1)
public class ImplForInterfaceForTransient implements InterfaceForTransient{

    @Transient
    public String getKey() {
        return "key";
    }

}
