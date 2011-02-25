package com.griddynamics.gemfire.serialization.codegen.impl.primitive;

/**
 * Generate code that
 * 1) read 'byte' field value from bean-setter
 * 2) write to dataOutput
 * for method DataSerializer.toData(...)
 * @author igolovach
 */

public class ToDataFieldPByteProcessor extends ToDataPrimitiveProcessor {
    public ToDataFieldPByteProcessor() {
        super("writeByte");
    }
}
