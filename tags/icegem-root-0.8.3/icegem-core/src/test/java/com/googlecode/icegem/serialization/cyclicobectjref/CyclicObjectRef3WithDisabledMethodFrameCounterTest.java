package com.googlecode.icegem.serialization.cyclicobectjref;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import com.googlecode.icegem.serialization.primitive.TestParent;

import javassist.CannotCompileException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author igolovach
 */

public class CyclicObjectRef3WithDisabledMethodFrameCounterTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), CyclicObjectRef3BeanA.class, CyclicObjectRef3BeanB.class, CyclicObjectRef3BeanC.class);
    }

    @Test(expectedExceptions = StackOverflowError.class)
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
