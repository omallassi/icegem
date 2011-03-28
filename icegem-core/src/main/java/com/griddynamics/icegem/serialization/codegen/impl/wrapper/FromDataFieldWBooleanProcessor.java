package com.griddynamics.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Boolean' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWBooleanProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWBooleanProcessor() {
        super("Boolean", "readBoolean");
    }
}
