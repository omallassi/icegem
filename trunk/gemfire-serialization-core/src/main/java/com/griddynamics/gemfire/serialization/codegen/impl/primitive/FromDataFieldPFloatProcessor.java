package com.griddynamics.gemfire.serialization.codegen.impl.primitive;

/**
 * Generate code that
 * 1) read 'float' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldPFloatProcessor extends FromDataPrimitiveProcessor {
    public FromDataFieldPFloatProcessor() {
        super("readFloat");
    }
}
