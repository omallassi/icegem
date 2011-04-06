package com.googlecode.icegem.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * //todo: doc
 * Annotation on class for that we create DataSerializer.
 *
 * @author igolovach
 */

@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface AutoSerializable {
    int dataSerializerID();
}
