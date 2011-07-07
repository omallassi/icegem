package com.googlecode.icegem.serialization.codegen;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.gemstone.bp.edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author igolovach
 */
@RunWith(Parameterized.class)
public class IntrospectorConstructorFailTest {

	Class<?> clazz;
	
	public IntrospectorConstructorFailTest(Class<?> clazz) {
		this.clazz = clazz;
	}
	
    @Parameters
    public static Collection<Object[]> dataConstructorFail() {
        return Arrays.asList(new Object[][]{
                // no-arg
                new Object[]{Integer.class},
                // public
                new Object[]{ClassWithNoPublicConstructor.class},
                // throws exceptions
                new Object[]{ClassWithWithConstructorThrowsExceptions.class},
        });
    }

    @Test(expected = InvalidClassException.class)
    public void testCheckValidConstructor_fail() throws InvalidClassException {
        Constructor<?> c = Introspector.checkConstructorNoArg(clazz);
        Introspector.checkConstructorWithoutExceptions(c);
        Introspector.checkConstructorPublic(c);
    }

    @Test(expected = InvalidClassException.class)
    public void testCheckClassIsPublic_fail() throws InvalidClassException {
        Introspector.checkClassIsPublic(NonPublicClass.class);
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