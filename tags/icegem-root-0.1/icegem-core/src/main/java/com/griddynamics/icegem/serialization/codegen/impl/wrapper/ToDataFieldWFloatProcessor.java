package com.griddynamics.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Float' field value from bean getter
 * 2) write it to DataOutput
 * for method DataSerializer.toData(...)
 * @author igolovach
 */

public class ToDataFieldWFloatProcessor extends ToDataFieldWrapperProcessor {

    public ToDataFieldWFloatProcessor() {
        super("floatValue", "writeFloat");
    }
}
