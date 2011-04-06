package com.googlecode.icegem.serialization.codegen;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.icegem.serialization.codegen.Introspector;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * @author igolovach
 */

public class IntrospectorTest {

    @DataProvider(name = "constructor-Ok")
    public Object[][] dataConstructorOk() {
        return new Object[][]{
                new Object[]{Object.class},
                new Object[]{ArrayList.class},
        };
    }

    @DataProvider(name = "constructor-Fail")
    public Object[][] dataConstructorFail() {
        return new Object[][]{
                // no-arg
                new Object[]{Integer.class},
                // public
                new Object[]{ClassWithNoPublicConstructor.class},
                // throws exceptions
                new Object[]{ClassWithWithConstructorThrowsExceptions.class},
        };
    }

    @Test(dataProvider = "constructor-Ok")
    public void testCheckValidConstructor_ok(Class<?> clazz) throws InvalidClassException {
        Constructor<?> c = Introspector.checkConstructorNoArg(clazz);
        Introspector.checkConstructorWithoutExceptions(c);
        Introspector.checkConstructorPublic(c);
    }

    @Test(dataProvider = "constructor-Fail", expectedExceptions = InvalidClassException.class)
    public void testCheckValidConstructor_fail(Class<?> clazz) throws InvalidClassException {
        testCheckValidConstructor_ok(clazz);
    }

    @Test
    public void testCheckClassIsPublic_ok() throws InvalidClassException {
        Introspector.checkClassIsPublic(Object.class);
    }

    @Test(expectedExceptions = InvalidClassException.class)
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