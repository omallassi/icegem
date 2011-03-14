package com.griddynamics.gemfire.serialization.cyclicobectjref;

import com.griddynamics.gemfire.serialization.primitive.TestParent;
import com.griddynamics.gemfire.serialization.codegen.MethodFrameCounter;
import org.testng.annotations.Test;

/**
 * @author igolovach
 */

public class CyclicObjectRef1Test extends TestParent {

    @Test(expectedExceptions = StackOverflowError.class, expectedExceptionsMessageRegExp = MethodFrameCounter.MSG)
    public void test() {
        // create test bean
        CyclicObjectRef1Bean expected = new CyclicObjectRef1Bean();
        expected.setNext(expected);

        // Register / Serialize / Deserialize
        CyclicObjectRef1Bean actual = registerSerializeAndDeserialize(expected);
    }
}
