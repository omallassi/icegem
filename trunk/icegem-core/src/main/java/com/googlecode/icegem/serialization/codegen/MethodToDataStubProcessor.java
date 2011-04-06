package com.googlecode.icegem.serialization.codegen;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer stub-method toData()
 * in two-phase compilation.
 *
 * @author igolovach
 */

public class MethodToDataStubProcessor {

    public String process(XClass element) {
        StringBuilder builder = new StringBuilder();

        builder.append("public boolean toData(Object obj, java.io.DataOutput out) throws java.io.IOException {\n")
                .append(tab("return true;\n"))
                .append("}\n");

        return builder.toString();
    }
}
