package com.griddynamics.icegem.serialization.codegen;

/**
 * Listener of generated DataSerializer-sources by {@link DataSerializerGenerator DataSerializerGenerator}.
 *
 * @see DataSerializerGenerator
 * @see com.griddynamics.icegem.serialization.HierarchyRegistry
 * @author igolovach
 */

public interface CodeGenerationListener {

    //todo: good signature?
    public void generated(String forClassName, String generatedClassName, String generatedClassBody);
}
