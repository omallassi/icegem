package com.googlecode.icegem.serialization.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InvalidClassException;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;

/**
 * @author igolovach
 */

public class WrapperTest extends TestParent {

    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), WrapperBean.class);
    }

    @Test
    public void testWrapperBean() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // create test bean
        WrapperBean expected = produceWrapperBean();

        // Serialize / Deserialize
        WrapperBean actual = (WrapperBean) serializeAndDeserialize(expected);

        // assert
        assertEquals(actual.getBoolean_(),expected.getBoolean_());
        assertEquals(actual.getByte_(),expected.getByte_());
        assertEquals(actual.getShort_(),expected.getShort_());
        assertEquals(actual.getCharacter_(),expected.getCharacter_());
        assertEquals(actual.getInteger_(),expected.getInteger_());
        assertEquals(actual.getLong_(),expected.getLong_());
        assertEquals(actual.getFloat_(),expected.getFloat_());
        assertEquals(actual.getDouble_(),expected.getDouble_());
    }

    @Test
    public void testWrapperBeanNullFields() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // create test bean
        WrapperBean expected = new WrapperBean();

        // Serialize / Deserialize
        WrapperBean actual = (WrapperBean) serializeAndDeserialize(expected);

        // assert
        assertNull((Object) actual.getBoolean_());
        assertNull((Object) actual.getByte_());
        assertNull((Object) actual.getShort_());
        assertNull((Object) actual.getCharacter_());
        assertNull((Object) actual.getInteger_());
        assertNull((Object) actual.getLong_());
        assertNull((Object) actual.getFloat_());
        assertNull((Object) actual.getDouble_());
    }

    private WrapperBean produceWrapperBean() {
        WrapperBean result = new WrapperBean();

        result.setBoolean_(true);
        result.setByte_((byte) 1);
        result.setShort_((short) 2);
        result.setCharacter_((char) 3);
        result.setInteger_((int) 4);
        result.setLong_((long) 5);
        result.setFloat_((float) 666.666f);
        result.setDouble_((double) 777.777d);

        return result;
    }
}
