package com.googlecode.icegem.serialization.primitive;

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
public class ObjectArrayCycleWithEnabledMethodFrameCounterTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // enable method frame counter by setting property
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "true");
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), ObjectArrayBean.class);
    }

    @AfterClass
    public void after() {
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "false");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCycle1() {
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        expected.setObjArr(new Object[]{expected});

        // Serialize / Deserialize
        serializeAndDeserialize(expected);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCycle2() {
        // create test bean
        ObjectArrayBean expectedA = new ObjectArrayBean();
        ObjectArrayBean expectedB = new ObjectArrayBean();
        expectedA.setObjArr(new Object[]{expectedB});
        expectedB.setObjArr(new Object[]{expectedA});

        // Serialize / Deserialize
        serializeAndDeserialize(expectedA);
    }
}