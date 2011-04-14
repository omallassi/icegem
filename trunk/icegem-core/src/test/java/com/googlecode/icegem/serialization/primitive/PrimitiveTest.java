package com.googlecode.icegem.serialization.primitive;

import com.googlecode.icegem.serialization.HierarchyRegistry;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InvalidClassException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author igolovach
 */
public class PrimitiveTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
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
        assertThat(actual.isBool()).isEqualTo(expected.isBool());
        assertThat(actual.getBool()).isEqualTo(expected.getBool());
        assertThat(actual.getByt()).isEqualTo(expected.getByt());
        assertThat(actual.getSh()).isEqualTo(expected.getSh());
        assertThat(actual.getCh()).isEqualTo(expected.getCh());
        assertThat(actual.getIn()).isEqualTo(expected.getIn());
        assertThat(actual.getL()).isEqualTo(expected.getL());
        assertThat(actual.getF()).isEqualTo(expected.getF());
        assertThat(actual.getD()).isEqualTo(expected.getD());
        assertThat(actual.isB1()).isEqualTo(expected.isB1());
        assertThat(actual.isB2()).isEqualTo(expected.isB2());
        assertThat(actual.isB3()).isEqualTo(expected.isB3());
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
