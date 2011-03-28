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

public class PrimitiveMultidimensionalArrayTest extends TestParent {

    // ----------------------- SYSTEM
    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
//        DataSerializerGenerator.registerCodeGenerationListener(new SOUTCodeGenerationListener());
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), PrimitiveMultidimensionalArrayBean.class);
    }

    @DataProvider(name = "data")
    public Object[][] date() {
        return new Object[][]{
                new Object[]{produceEmpty()},
                new Object[]{produceFullStructureButEmptyData()},
                new Object[]{producePrimitiveArrayBean()},
        };
    }

    // ----------------------- TESTS
    @Test(dataProvider = "data")
    public void testPrimitiveArrayBean(PrimitiveMultidimensionalArrayBean expected) throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // Serialize / Deserialize
        PrimitiveMultidimensionalArrayBean actual = serializeAndDeserialize(expected);

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
        PrimitiveMultidimensionalArrayBean expected = new PrimitiveMultidimensionalArrayBean();

        // Serialize / Deserialize
        PrimitiveMultidimensionalArrayBean actual = serializeAndDeserialize(expected);

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

    // ----------------------- PRODUCERS
    private PrimitiveMultidimensionalArrayBean producePrimitiveArrayBean() {
        final PrimitiveMultidimensionalArrayBean result = new PrimitiveMultidimensionalArrayBean();

        //todo: uncomment
//        result.setBooleanArray(new boolean[]{true, false});
//        result.setByteArray(new byte[]{0, -1, +2, -3, +4, -5, +6, -7, +8, -9});
//        result.setShortArray(new short[][][]{null, new char[][]{}, new char[][]{null, new char[]{}, new char[]{'a', '\u0000', '\uFFFF'}}});
        result.setCharArray(new char[][][]{null, new char[][]{}, new char[][]{null, new char[]{}, new char[]{'a', '\u0000', '\uFFFF'}}});
//        result.setIntArray(new int[]{0, -1, +2, -3, +4, -5, +6, -7, +8, -9});
//        result.setLongArray(new long[]{0, -1, +2, -3, +4, -5, +6, -7, +8, -9});
//        result.setFloatArray(new float[]{0, -1.1f, +2.2f, -3.3f, +4.4f, -5.5f, +6.6f, -7.7f, +8.8f, -9.9f});
//        result.setDoubleArray(new double[]{0, -1.11d, +2.22d, -3.33d, +4.44d, -5.55d, +6.66d, -7.77d, +8.88d, -9.99d});

        return result;
    }

    private PrimitiveMultidimensionalArrayBean produceEmpty() {
        final PrimitiveMultidimensionalArrayBean result = new PrimitiveMultidimensionalArrayBean();

        result.setBooleanArray(new boolean[][][]{});
        result.setByteArray(new byte[][][]{});
        result.setShortArray(new short[][][]{});
        result.setCharArray(new char[][][]{});
        result.setIntArray(new int[][][]{});
        result.setLongArray(new long[][][]{});
        result.setFloatArray(new float[][][]{});
        result.setDoubleArray(new double[][][]{});

        return result;
    }

    private PrimitiveMultidimensionalArrayBean produceFullStructureButEmptyData() {
        final PrimitiveMultidimensionalArrayBean result = new PrimitiveMultidimensionalArrayBean();

        //todo: all strings the same?
        result.setBooleanArray(new boolean[][][]{new boolean[][]{new boolean[]{}, new boolean[]{}}, new boolean[][]{new boolean[]{}, new boolean[]{}}});
        result.setByteArray(new byte[][][]{new byte[][]{new byte[]{}, new byte[]{}}, new byte[][]{new byte[]{}, new byte[]{}}});
        result.setShortArray(new short[][][]{null});
        result.setCharArray(new char[][][]{new char[][]{new char[]{}, new char[]{}}, new char[][]{new char[]{}, new char[]{}}});
        result.setIntArray(new int[][][]{});
        result.setLongArray(new long[][][]{});
        result.setFloatArray(new float[][][]{});
        result.setDoubleArray(new double[][][]{});

        return result;
    }
}
