package com.griddynamics.gemfire.serialization.codegen.impl.primitive;

/**
 * Generate code that
 * 1) read 'int' field value from bean-setter
 * 2) write to dataOutput
 * for method DataSerializer.toData(...)
 * @author igolovach
 */

public class ToDataFieldPIntProcessor extends ToDataPrimitiveProcessor {
    public ToDataFieldPIntProcessor() {
        super("writeInt");
    }
}
