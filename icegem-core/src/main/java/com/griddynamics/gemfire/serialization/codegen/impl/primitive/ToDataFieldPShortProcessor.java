package com.griddynamics.gemfire.serialization.codegen.impl.primitive;

/**
 * Generate code that
 * 1) read 'short' field value from bean-setter
 * 2) write to dataOutput
 * for method DataSerializer.toData(...)
 * @author igolovach
 */

public class ToDataFieldPShortProcessor extends ToDataPrimitiveProcessor {
    public ToDataFieldPShortProcessor() {
        super("writeShort");
    }
}
