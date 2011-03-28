package com.griddynamics.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Long' field value from bean getter
 * 2) write it to DataOutput
 * for method DataSerializer.toData(...)
 * @author igolovach
 */

public class ToDataFieldWLongProcessor extends ToDataFieldWrapperProcessor {

    public ToDataFieldWLongProcessor() {
        super("longValue", "writeLong");
    }
}
