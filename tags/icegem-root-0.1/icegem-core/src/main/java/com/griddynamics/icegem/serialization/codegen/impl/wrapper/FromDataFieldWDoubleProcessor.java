package com.griddynamics.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Double' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWDoubleProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWDoubleProcessor() {
        super("Double", "readDouble");
    }
}
