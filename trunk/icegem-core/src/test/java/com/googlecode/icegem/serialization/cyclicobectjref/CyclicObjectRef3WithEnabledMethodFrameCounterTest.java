package com.googlecode.icegem.serialization.cyclicobectjref;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import com.googlecode.icegem.serialization.primitive.TestParent;

/**
 * @author Andrey Stepanov aka standy
 */
public class CyclicObjectRef3WithEnabledMethodFrameCounterTest extends TestParent {
    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // enable method frame counter by setting property
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "true");
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), CyclicObjectRef3BeanA.class, CyclicObjectRef3BeanB.class, CyclicObjectRef3BeanC.class);
    }

    @AfterClass
    public static void after() {
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "false");
    }

    @Test(expected = RuntimeException.class)
    public void test(){
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
