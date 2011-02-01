package com.griddynamics.gemfire.serialization.codegen.impl.primitive;

/**
 * Generate code that
 * 1) read 'byte' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldPByteProcessor extends FromDataPrimitiveProcessor {
    public FromDataFieldPByteProcessor() {
        super("readByte");
    }
}
