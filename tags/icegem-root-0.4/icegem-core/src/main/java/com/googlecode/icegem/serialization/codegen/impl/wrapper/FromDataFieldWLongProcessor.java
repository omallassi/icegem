package com.googlecode.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Long' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWLongProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWLongProcessor() {
        super("Long", "readLong");
    }
}
