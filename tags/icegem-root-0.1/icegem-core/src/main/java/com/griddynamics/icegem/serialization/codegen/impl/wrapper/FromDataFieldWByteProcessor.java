package com.griddynamics.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Byte' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWByteProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWByteProcessor() {
        super("Byte", "readByte");
    }
}
