package com.googlecode.icegem.serialization.primitive;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import static org.junit.Assert.*;

/**
 * @author igolovach
 */

public class ObjectTest extends TestParent {
    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), ObjectBean.class);
    }


    @Test
    public void testNull() {
        // create test bean
        ObjectBean expected = new ObjectBean();

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertNull(actual.getObj());
    }

    @Test(expected = RuntimeException.class) //NotSerializableException wrapped in RuntimeException by TestParent
    public void testObject() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj(new Object());

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertNull(actual.getObj());
    }

    @Test
    public void testString() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj("Hello!");

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertTrue(actual.getObj() instanceof String);
        assertEquals(actual.getObj(), expected.getObj());
    }

    @Test
    public void testInteger(){
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj(123456);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertTrue(actual.getObj() instanceof Integer);
        assertEquals(actual.getObj(), expected.getObj());
    }

    @Test(expected = StackOverflowError.class)
    public void testCycle1() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj(expected);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);
    }

    @Test(expected = StackOverflowError.class)
    public void testCycle2() {
        // create test bean
        ObjectBean expectedA = new ObjectBean();
        ObjectBean expectedB = new ObjectBean();
        expectedA.setObj(expectedB);
        expectedB.setObj(expectedA);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expectedA);
    }

    @Test
    public void testSequence(){
        // create test bean
        ObjectBean expectedA = new ObjectBean();
        ObjectBean expectedB = new ObjectBean();
        ObjectBean expectedC = new ObjectBean();
        expectedA.setObj(expectedB);
        expectedB.setObj(expectedC);
        expectedC.setObj(null);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expectedA);

        // assert
        assertTrue(actual.getObj() instanceof ObjectBean);
        assertTrue(((ObjectBean) actual.getObj()).getObj() instanceof ObjectBean);
        assertNull(((ObjectBean) ((ObjectBean) actual.getObj()).getObj()).getObj());
    }

    @Test
    public void testObjectArrayZeroLength() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        Object[] expectedArr = new Object[0];
        expected.setObj(expectedArr);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertNotNull((Object[])actual.getObj());
        assertEquals(((Object[]) actual.getObj()).length, expectedArr.length);
    }

    @Test
    public void testObjectArrayWithNulls() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        Object[] expectedArr = new Object[]{null, null, null};
        expected.setObj(expectedArr);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert§
        assertTrue(actual.getObj() instanceof Object[]);
        assertEquals(((Object[]) actual.getObj()).length, expectedArr.length);
        assertEquals(((Object[]) actual.getObj()), expectedArr);
    }
}

