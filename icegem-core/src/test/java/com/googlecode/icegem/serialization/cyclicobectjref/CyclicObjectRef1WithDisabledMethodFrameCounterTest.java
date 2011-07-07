package com.googlecode.icegem.serialization.cyclicobectjref;

import org.junit.Test;

import com.googlecode.icegem.serialization.primitive.TestParent;

/**
 * @author Andrey Stepanov aka standy
 */
public class CyclicObjectRef1WithDisabledMethodFrameCounterTest extends TestParent {
    @Test(expected = StackOverflowError.class)
    public void test() {
        // create test bean
        CyclicObjectRef1Bean expected = new CyclicObjectRef1Bean();
        expected.setNext(expected);

        // Register / Serialize / Deserialize
        registerSerializeAndDeserialize(expected);
    }
}
