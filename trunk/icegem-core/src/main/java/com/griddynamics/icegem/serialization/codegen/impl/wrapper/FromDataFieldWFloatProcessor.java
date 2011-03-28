package com.griddynamics.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Float' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWFloatProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWFloatProcessor() {
        super("Float", "readFloat");
    }
}
