package com.googlecode.icegem.serialization.primitive;

import com.gemstone.gemfire.ToDataException;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import javassist.CannotCompileException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author Andrey Stepanov aka standy
 */
public class ObjectCycleWithEnabledMethodFrameCounterTest extends TestParent {
    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // enable method frame counter by setting property
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "true");
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), ObjectBean.class);
    }

    @AfterClass
    public void after() {
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "false");
    }

    @Test(expectedExceptions = ToDataException.class)
    public void testCycle1() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj(expected);

        // Serialize / Deserialize
        serializeAndDeserialize(expected);
    }

    @Test(expectedExceptions = ToDataException.class)
    public void testCycle2() {
        // create test bean
        ObjectBean expectedA = new ObjectBean();
        ObjectBean expectedB = new ObjectBean();
        expectedA.setObj(expectedB);
        expectedB.setObj(expectedA);

        // Serialize / Deserialize
        serializeAndDeserialize(expectedA);
    }
}