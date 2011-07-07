package com.googlecode.icegem.serialization.primitive;

import java.io.IOException;
import java.io.InvalidClassException;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import static org.junit.Assert.*;

/**
 * @author igolovach
 */
public class PrimitiveTest extends TestParent {

    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), PrimitiveBean.class);
    }

    @Test
    public void testPrimitiveBean() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // create test bean
        PrimitiveBean expected = producePrimitiveBean();

        // Serialize / Deserialize
        PrimitiveBean actual = serializeAndDeserialize(expected);

        // assert
        assertEquals(actual.isBool(), expected.isBool());
        assertEquals(actual.getBool(), expected.getBool());
        assertEquals(actual.getByt(), expected.getByt());
        assertEquals(actual.getSh(), expected.getSh());
        assertEquals(actual.getCh(), expected.getCh());
        assertEquals(actual.getIn(), expected.getIn());
        assertEquals(actual.getL(), expected.getL());
        assertEquals(actual.getF(), expected.getF(), 0.001f);
        assertEquals(actual.getD(), expected.getD(), 0.001);
        assertEquals(actual.isB1(), expected.isB1());
        assertEquals(actual.isB2(), expected.isB2());
        assertEquals(actual.isB3(), expected.isB3());
    }

    private PrimitiveBean producePrimitiveBean() {
        PrimitiveBean result = new PrimitiveBean();

        result.setBool(true);
        result.setByt((byte) 1);
        result.setSh((short) 2);
        result.setCh((char) 3);
        result.setIn(4);
        result.setL(5);
        result.setF(666.666f);
        result.setD(777.777d);

        return result;
    }
}
