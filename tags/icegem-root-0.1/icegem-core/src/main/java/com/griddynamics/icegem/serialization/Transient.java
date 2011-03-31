package com.griddynamics.icegem.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * Annotation on getter-s that not serialized.
 *
 * @author igolovach
 */

@Target(METHOD)
@Retention(RUNTIME)
public @interface Transient {
}
