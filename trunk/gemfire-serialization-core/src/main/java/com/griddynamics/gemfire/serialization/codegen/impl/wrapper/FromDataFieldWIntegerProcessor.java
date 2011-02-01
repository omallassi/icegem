package com.griddynamics.gemfire.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Integer' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWIntegerProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWIntegerProcessor() {
        super("Integer", "readInt");
    }
}
