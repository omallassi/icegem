package com.googlecode.icegem.serialization.cyclicobectjref;

import com.googlecode.icegem.serialization.primitive.TestParent;
import org.testng.annotations.Test;

/**
 * @author Andrey Stepanov aka standy
 */
public class CyclicObjectRef1WithDisabledMethodFrameCounterTest extends TestParent {
    @Test(expectedExceptions = StackOverflowError.class)
    public void test() {
        // create test bean
        CyclicObjectRef1Bean expected = new CyclicObjectRef1Bean();
        expected.setNext(expected);

        // Register / Serialize / Deserialize
        registerSerializeAndDeserialize(expected);
    }
}
