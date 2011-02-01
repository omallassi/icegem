package com.griddynamics.gemfire.serialization.codegen;

/**
 * Simple realization of {@link CodeGenerationListener CodeGenerationListener} that write sources to System.out.
 *
 * @see CodeGenerationListener
 * @author igolovach
 */

public class SOUTCodeGenerationListener implements CodeGenerationListener {

    public void generated(String forClassName, String generatedClassName, String generatedClassBody) {
        System.out.println("--------------------------- for: " + forClassName);
        System.out.println(generatedClassBody);
    }
}
