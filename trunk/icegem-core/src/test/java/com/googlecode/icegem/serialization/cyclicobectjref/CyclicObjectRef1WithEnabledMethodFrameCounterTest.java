package com.googlecode.icegem.serialization.cyclicobectjref;

import com.gemstone.gemfire.ToDataException;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import com.googlecode.icegem.serialization.primitive.TestParent;

import javassist.CannotCompileException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author igolovach
 */

public class CyclicObjectRef1WithEnabledMethodFrameCounterTest extends TestParent {
    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // enable method frame counter by setting property
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "true");
    }

    @AfterClass
    public void after() {
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "false");
    }

    @Test(expectedExceptions = ToDataException.class)
    public void test() {
        System.setProperty(MethodFrameCounter.SYSTEM_PROPERTY_NAME, "true");
        // create test bean
        CyclicObjectRef1Bean expected = new CyclicObjectRef1Bean();
        expected.setNext(expected);
        // Register / Serialize / Deserialize
        registerSerializeAndDeserialize(expected);
    }
}
