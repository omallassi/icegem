package com.griddynamics.gemfire.serialization.codegen;

/**
 * Listener of generated DataSerializer-sources by {@link DataSerializerGenerator DataSerializerGenerator}.
 *
 * @see DataSerializerGenerator
 * @see com.griddynamics.gemfire.serialization.HierarchyRegistry
 * @author igolovach
 */

public interface CodeGenerationListener {

    //todo: good signature?
    public void generated(String forClassName, String generatedClassName, String generatedClassBody);
}
