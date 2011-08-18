package com.googlecode.icegem.serialization;


/**
 * Global configuration of icegem facilities.
 *
 * @author igolovach
 */

public class Configuration {
	private static boolean JAVA_SERIALIZATION_ENABLED = !Boolean.getBoolean("icegem.serialization.java.native.disabled");
	
	private static boolean DISTRIBUTE_DESERIALIZER_REGISTRATION = !Boolean.getBoolean("icegem.serialization.distribute.registration.disabled");
	
    public Configuration() {
    }

    /**
     * Current Configuration: loaded at framework startup.
     * You can cache it: don't reloaded, don't change at runtime.
     */
    public static Configuration get() {
        return new Configuration();
    }

    public boolean isJavaSerializationEnabled() {
        return JAVA_SERIALIZATION_ENABLED;
    }
    
    public void setJavaSerializationEnabled(boolean value) {
    	JAVA_SERIALIZATION_ENABLED = value;
    }
    
    public boolean isDeserializerRegistrationDistributed() {
    	return DISTRIBUTE_DESERIALIZER_REGISTRATION ;
    }
    
    public void setDeserializerRegistrationDistributed(boolean value) {
    	DISTRIBUTE_DESERIALIZER_REGISTRATION = value;
    }

    /**
     * All *DataSerializer-s will be created in this package.
     * @return root package
     */
    public String getDataSerializerPackage() {
        return "com.googlecode.icegem.serialization.$$$";
    }
}
