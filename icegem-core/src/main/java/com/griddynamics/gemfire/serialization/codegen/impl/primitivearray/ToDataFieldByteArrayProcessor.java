package com.griddynamics.gemfire.serialization.codegen.impl.primitivearray;

/**
 * Optimized variant in comparing with ToDataField***ArrayProcessor for other primitive arrays.
 *
 * @author igolovach
 */

public class ToDataFieldByteArrayProcessor extends ToDataFieldPrimitiveArrayProcessor {

    public ToDataFieldByteArrayProcessor() {
        super("writeByteArray");
    }
}
