package com.googlecode.icegem.serialization._inheritance.transientgetter;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.Transient;

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
