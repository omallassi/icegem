package com.griddynamics.gemfire.serialization.codegen.impl.primitive;

/**
 * Generate code that
 * 1) read 'Double' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldPDoubleProcessor extends FromDataPrimitiveProcessor {
    public FromDataFieldPDoubleProcessor() {
        super("readDouble");
    }
}
