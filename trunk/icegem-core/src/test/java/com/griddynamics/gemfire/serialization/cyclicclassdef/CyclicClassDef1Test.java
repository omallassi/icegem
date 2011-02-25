package com.griddynamics.gemfire.serialization.cyclicclassdef;

import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.TestParent;
import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author igolovach
 */

public class CyclicClassDef1Test extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), CyclicClassDef1Bean.class);
    }

    @Test
    public void testWithNullField() {
        // create test bean
        CyclicClassDef1Bean expected = new CyclicClassDef1Bean();

        // Serialize / Deserialize
        CyclicClassDef1Bean actual = (CyclicClassDef1Bean) serializeAndDeserialize(expected);

        // assert Company correct
        Assertions.assertThat(actual.getData()).isEqualTo(expected.getData());
        Assertions.assertThat(actual.getNext()).isEqualTo(null);
    }

    @Test
    public void test()  {
        // create test bean
        CyclicClassDef1Bean expectedA = new CyclicClassDef1Bean();
        CyclicClassDef1Bean expectedB = new CyclicClassDef1Bean();
        CyclicClassDef1Bean expectedC = new CyclicClassDef1Bean();
        expectedA.setData(111);
        expectedA.setNext(expectedB);
        expectedB.setData(222);
        expectedB.setNext(expectedC);
        expectedC.setData(333);
        expectedC.setNext(null);

        // Serialize / Deserialize
        CyclicClassDef1Bean actualA = (CyclicClassDef1Bean) serializeAndDeserialize(expectedA);
        CyclicClassDef1Bean actualB = actualA.getNext();
        CyclicClassDef1Bean actualC = actualB.getNext();

        // assert Company correct
        Assertions.assertThat(actualA.getData()).isEqualTo(expectedA.getData());
        Assertions.assertThat(actualB.getData()).isEqualTo(expectedB.getData());
        Assertions.assertThat(actualC.getData()).isEqualTo(expectedC.getData());
        Assertions.assertThat(actualC.getNext()).isEqualTo(null);
    }
}
