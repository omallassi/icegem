package com.googlecode.icegem.serialization.cyclicobectjref;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import com.googlecode.icegem.serialization.primitive.TestParent;
import javassist.CannotCompileException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author Andrey Stepanov aka standy
 */
public class CyclicObjectRef3WithEnabledMethodFrameCounterTest extends TestParent {
    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // enable method frame counter by setting property
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "true");
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), CyclicObjectRef3BeanA.class, CyclicObjectRef3BeanB.class, CyclicObjectRef3BeanC.class);
    }

    @AfterClass
    public void after() {
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "false");
    }

    @Test(expectedExceptions = RuntimeException.class)
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
