package com.googlecode.icegem.serialization.cyclicobectjref;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import com.googlecode.icegem.serialization.primitive.TestParent;

/**
 * @author igolovach
 */

public class CyclicObjectRef3WithDisabledMethodFrameCounterTest extends TestParent {

    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), CyclicObjectRef3BeanA.class, CyclicObjectRef3BeanB.class, CyclicObjectRef3BeanC.class);
    }

    @Test(expected = StackOverflowError.class)
    public void testWithDisabledMethodFrameCounter(){
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "false");
        // create test bean
        CyclicObjectRef3BeanA expectedA = new CyclicObjectRef3BeanA();
        CyclicObjectRef3BeanB expectedB = new CyclicObjectRef3BeanB();
        CyclicObjectRef3BeanC expectedC = new CyclicObjectRef3BeanC();
        expectedA.setNext(expectedB);
        expectedB.setNext(expectedC);
        expectedC.setNext(expectedA);

        serializeAndDeserialize(expectedA);
    }
}
