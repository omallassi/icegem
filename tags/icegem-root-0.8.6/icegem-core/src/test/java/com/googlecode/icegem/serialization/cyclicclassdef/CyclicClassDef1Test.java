package com.googlecode.icegem.serialization.cyclicclassdef;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import static org.junit.Assert.*;

/**
 * @author igolovach
 */

public class CyclicClassDef1Test extends TestParent {

    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), CyclicClassDef1Bean.class);
    }

    @Test
    public void testWithNullField() {
        // create test bean
        CyclicClassDef1Bean expected = new CyclicClassDef1Bean();

        // Serialize / Deserialize
        CyclicClassDef1Bean actual = (CyclicClassDef1Bean) serializeAndDeserialize(expected);

        // assert Company correct
        assertEquals(actual.getData(), expected.getData());
        assertNull(actual.getNext());
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
        assertEquals(actualA.getData(), expectedA.getData());
        assertEquals(actualB.getData(), expectedB.getData());
        assertEquals(actualC.getData(), expectedC.getData());
        assertNull(actualC.getNext());
    }
}
