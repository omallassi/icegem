package com.griddynamics.gemfire.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Integer' field value from bean getter
 * 2) write it to DataOutput
 * for method DataSerializer.toData(...)
 * @author igolovach
 */

public class ToDataFieldWIntegerProcessor extends ToDataFieldWrapperProcessor {

    public ToDataFieldWIntegerProcessor() {
        super("intValue", "writeInt");
    }
}
