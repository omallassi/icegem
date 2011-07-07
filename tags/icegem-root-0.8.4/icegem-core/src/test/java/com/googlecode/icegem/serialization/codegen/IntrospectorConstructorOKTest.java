package com.googlecode.icegem.serialization.codegen;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author igolovach
 */
@RunWith(Parameterized.class)
public class IntrospectorConstructorOKTest {
	Class<?> clazz;
	
	public IntrospectorConstructorOKTest(Class<?> clazz) {
		this.clazz = clazz;
	}

    @Parameters
    public static Collection<Object[]> dataConstructorOk() {
        return Arrays.asList(new Object[][]{
                new Object[]{Object.class},
                new Object[]{ArrayList.class},
        });
    }

    @Test
    public void testCheckValidConstructor_ok() throws InvalidClassException {
        Constructor<?> c = Introspector.checkConstructorNoArg(clazz);
        Introspector.checkConstructorWithoutExceptions(c);
        Introspector.checkConstructorPublic(c);
    }

    @Test
    public void testCheckClassIsPublic_ok() throws InvalidClassException {
        Introspector.checkClassIsPublic(Object.class);
    }

    private static class NonPublicClass {
    }

    public static class ClassWithNoPublicConstructor {
        protected ClassWithNoPublicConstructor() {
        }
    }

    public static class ClassWithWithConstructorThrowsExceptions {
        protected ClassWithWithConstructorThrowsExceptions() throws IOException {
        }
    }
}