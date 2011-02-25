package com.griddynamics.gemfire.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Short' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWShortProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWShortProcessor() {
        super("Short", "readShort");
    }
}
