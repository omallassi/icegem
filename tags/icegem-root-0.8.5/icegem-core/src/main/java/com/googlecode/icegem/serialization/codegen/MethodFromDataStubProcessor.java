package com.googlecode.icegem.serialization.codegen;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * Code generator for DataSerializer method fromData()
 *
 * @author igolovach
 */

public class MethodFromDataStubProcessor {

    public String process(XClass element) {
        StringBuilder builder = new StringBuilder();
        
        // method header
        builder.append("public Object fromData(java.io.DataInput in) throws java.io.IOException, ClassNotFoundException {\n")
                .append(tab("return null;"))
                .append("}\n");

        return builder.toString();
    }
}
