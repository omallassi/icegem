package com.griddynamics.gemfire.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Short' field value from bean getter
 * 2) write it to DataOutput
 * for method DataSerializer.toData(...)
 * @author igolovach
 */

public class ToDataFieldWShortProcessor extends ToDataFieldWrapperProcessor {

    public ToDataFieldWShortProcessor() {
        super("shortValue", "writeShort");
    }
}
