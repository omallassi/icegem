package com.googlecode.icegem.serialization.primitive;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;

/**
 * @author Andrey Stepanov aka standy
 */
public class ObjectArrayCycleWithEnabledMethodFrameCounterTest extends TestParent {

    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // enable method frame counter by setting property
        MethodFrameCounter.ENABLED = true;
        // register
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), ObjectArrayBean.class);
    }

    @AfterClass
    public static void after() {
        MethodFrameCounter.ENABLED = false;
    }

    @Test(expected = RuntimeException.class)
    public void testCycle1() {
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        expected.setObjArr(new Object[]{expected});

        // Serialize / Deserialize
        serializeAndDeserialize(expected);
    }

    @Test(expected = RuntimeException.class)
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