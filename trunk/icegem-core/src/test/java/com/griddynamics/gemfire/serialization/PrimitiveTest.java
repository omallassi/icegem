package com.griddynamics.gemfire.serialization;

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
        assertThat(actual.getBoolean()).isEqualTo(expected.getBoolean());
        assertThat(actual.getByte()).isEqualTo(expected.getByte());
        assertThat(actual.getShort()).isEqualTo(expected.getShort());
        assertThat(actual.getChar()).isEqualTo(expected.getChar());
        assertThat(actual.getInt()).isEqualTo(expected.getInt());
        assertThat(actual.getLong()).isEqualTo(expected.getLong());
        assertThat(actual.getFloat()).isEqualTo(expected.getFloat());
        assertThat(actual.getDouble()).isEqualTo(expected.getDouble());
    }

    private PrimitiveBean producePrimitiveBean() {
        PrimitiveBean result = new PrimitiveBean();

        result.setBoolean(true);
        result.setByte((byte) 1);
        result.setShort((short) 2);
        result.setChar((char) 3);
        result.setInt(4);
        result.setLong(5);
        result.setFloat(666.666f);
        result.setDouble(777.777d);

        return result;
    }
}
