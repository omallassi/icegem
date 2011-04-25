package com.googlecode.icegem.serialization.codegen.impl.wrapper;

/**
 * Generate code that
 * 1) read 'Character' field value from DataInput
 * 2) set it to bean
 * for method DataSerializer.fromData(...)
 * @author igolovach
 */

public class FromDataFieldWCharacterProcessor extends FromDataFieldWrapperProcessor {

    public FromDataFieldWCharacterProcessor() {
        super("Character", "readChar");
    }
}
