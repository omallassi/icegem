package com.griddynamics.icegem.serialization.primitive;

import com.griddynamics.icegem.serialization.HierarchyRegistry;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.fest.assertions.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InvalidClassException;

/**
 * @author igolovach
 */

public class PrimitiveArrayTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), PrimitiveArrayBean.class);
    }

    @DataProvider(name = "data")
    public Object[][] date() {
        return new Object[][] {
                new Object[] {producePrimitiveArrayBean()},
                new Object[] {producePrimitiveArrayBeanFieldsZeroLength()},
        };
    }

    @Test(dataProvider = "data")
    public void testPrimitiveArrayBean(PrimitiveArrayBean expected ) throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // Serialize / Deserialize
        PrimitiveArrayBean actual = serializeAndDeserialize(expected);

        // assert
        Assertions.assertThat(actual.getBooleanArray()).isEqualTo(expected.getBooleanArray());
        Assertions.assertThat(actual.getByteArray()).isEqualTo(expected.getByteArray());
        Assertions.assertThat(actual.getShortArray()).isEqualTo(expected.getShortArray());
        Assertions.assertThat(actual.getCharArray()).isEqualTo(expected.getCharArray());
        Assertions.assertThat(actual.getIntArray()).isEqualTo(expected.getIntArray());
        Assertions.assertThat(actual.getLongArray()).isEqualTo(expected.getLongArray());
        Assertions.assertThat(actual.getFloatArray()).isEqualTo(expected.getFloatArray());
        Assertions.assertThat(actual.getDoubleArray()).isEqualTo(expected.getDoubleArray());
    }

    @Test
    public void testArrayBeanFieldIsNull() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // create test bean
        PrimitiveArrayBean expected = new PrimitiveArrayBean();

        // Serialize / Deserialize
        PrimitiveArrayBean actual = serializeAndDeserialize(expected);

        // assert
        Assertions.assertThat(actual.getBooleanArray()).isNull();
        Assertions.assertThat(actual.getByteArray()).isNull();
        Assertions.assertThat(actual.getShortArray()).isNull();
        Assertions.assertThat(actual.getCharArray()).isNull();
        Assertions.assertThat(actual.getIntArray()).isNull();
        Assertions.assertThat(actual.getLongArray()).isNull();
        Assertions.assertThat(actual.getFloatArray()).isNull();
        Assertions.assertThat(actual.getDoubleArray()).isNull();
    }

    private PrimitiveArrayBean producePrimitiveArrayBean() {
        final PrimitiveArrayBean result = new PrimitiveArrayBean();

        result.setBooleanArray(new boolean[]{true, false});
        result.setByteArray(new byte[]{0, -1, +2, -3, +4, -5, +6, -7, +8, -9});
        result.setShortArray(new short[]{0, -1, +2, -3, +4, -5, +6, -7, +8, -9});
        result.setCharArray(new char[]{0, 1, +2, 3, +4, 5, +6, 7, +8, 9});
        result.setIntArray(new int[]{0, -1, +2, -3, +4, -5, +6, -7, +8, -9});
        result.setLongArray(new long[]{0, -1, +2, -3, +4, -5, +6, -7, +8, -9});
        result.setFloatArray(new float[]{0, -1.1f, +2.2f, -3.3f, +4.4f, -5.5f, +6.6f, -7.7f, +8.8f, -9.9f});
        result.setDoubleArray(new double[]{0, -1.11d, +2.22d, -3.33d, +4.44d, -5.55d, +6.66d, -7.77d, +8.88d, -9.99d});

        return result;
    }

    private PrimitiveArrayBean producePrimitiveArrayBeanFieldsZeroLength() {
        final PrimitiveArrayBean result = new PrimitiveArrayBean();

        result.setBooleanArray(new boolean[]{});
        result.setByteArray(new byte[]{});
        result.setShortArray(new short[]{});
        result.setCharArray(new char[]{});
        result.setIntArray(new int[]{});
        result.setLongArray(new long[]{});
        result.setFloatArray(new float[]{});
        result.setDoubleArray(new double[]{});

        return result;
    }
}
