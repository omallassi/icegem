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

public class WrapperTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), WrapperBean.class);
    }

    @Test
    public void testWrapperBean() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // create test bean
        WrapperBean expected = produceWrapperBean();

        // Serialize / Deserialize
        WrapperBean actual = (WrapperBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getBoolean_()).isEqualTo(expected.getBoolean_());
        assertThat(actual.getByte_()).isEqualTo(expected.getByte_());
        assertThat(actual.getShort_()).isEqualTo(expected.getShort_());
        assertThat(actual.getCharacter_()).isEqualTo(expected.getCharacter_());
        assertThat(actual.getInteger_()).isEqualTo(expected.getInteger_());
        assertThat(actual.getLong_()).isEqualTo(expected.getLong_());
        assertThat(actual.getFloat_()).isEqualTo(expected.getFloat_());
        assertThat(actual.getDouble_()).isEqualTo(expected.getDouble_());
    }

    @Test
    public void testWrapperBeanNullFields() throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        // create test bean
        WrapperBean expected = new WrapperBean();

        // Serialize / Deserialize
        WrapperBean actual = (WrapperBean) serializeAndDeserialize(expected);

        // assert
        assertThat((Object) actual.getBoolean_()).isNull();
        assertThat((Object) actual.getByte_()).isNull();
        assertThat((Object) actual.getShort_()).isNull();
        assertThat((Object) actual.getCharacter_()).isNull();
        assertThat((Object) actual.getInteger_()).isNull();
        assertThat((Object) actual.getLong_()).isNull();
        assertThat((Object) actual.getFloat_()).isNull();
        assertThat((Object) actual.getDouble_()).isNull();
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
