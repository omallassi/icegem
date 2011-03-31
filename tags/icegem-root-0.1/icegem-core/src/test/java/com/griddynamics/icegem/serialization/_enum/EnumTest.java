package com.griddynamics.icegem.serialization._enum;

import com.griddynamics.icegem.serialization.HierarchyRegistry;
import com.griddynamics.icegem.serialization.primitive.TestParent;

import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author igolovach
 */

@Test(enabled = false) //todo: enable
public class EnumTest extends TestParent {

    @BeforeClass
    public void beforeClass() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), FieldEnumBean.class, SimpleEnumBean.class, ExtendedFinalEnumBean.class, ExtendedMutableEnumBean.class);
    }

    @Test(enabled = false) //todo: enable
    public void testRootSimple() {
        final SimpleEnumBean expected = SimpleEnumBean.A;

        // Serialize / Deserialize
        SimpleEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test(enabled = false) //todo: enable
    public void testRootExtendedFinal() {
        final ExtendedFinalEnumBean expected = ExtendedFinalEnumBean.X;

        // Serialize / Deserialize
        ExtendedFinalEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual).isEqualTo(expected);
    }    

    @Test(enabled = false) //todo: enable
    public void testRootExtendedMutable() {
        final ExtendedMutableEnumBean expected = ExtendedMutableEnumBean.M;

        // Serialize / Deserialize
        ExtendedMutableEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test(enabled = false) //todo: enable
    public void testField() throws NotSerializableException, CannotCompileException {
        // init
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setSimpleEnumBean(SimpleEnumBean.B);
        expected.setExtendedFinalEnumBean(ExtendedFinalEnumBean.Z);
        final ExtendedMutableEnumBean extendedMutableEnumBean = ExtendedMutableEnumBean.K;
        extendedMutableEnumBean.setMutableName("some mutable name");
        expected.setExtendedMutableEnumBean(extendedMutableEnumBean);

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getSimpleEnumBean()).isEqualTo(expected.getSimpleEnumBean());

        Assertions.assertThat(actual.getExtendedFinalEnumBean()).isEqualTo(expected.getExtendedFinalEnumBean());
        Assertions.assertThat(actual.getExtendedFinalEnumBean().getFinalName()).isEqualTo(expected.getExtendedFinalEnumBean().getFinalName());

        Assertions.assertThat(actual.getExtendedMutableEnumBean()).isEqualTo(expected.getExtendedMutableEnumBean());
        Assertions.assertThat(actual.getExtendedMutableEnumBean().getMutableName()).isEqualTo(expected.getExtendedMutableEnumBean().getMutableName());
    }

    @Test(enabled = false) //todo: enable
    public void testEnum() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setEnumField(SimpleEnumBean.B);

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getEnumField()).isEqualTo(expected.getEnumField());
    }

    @Test(enabled = false) //todo: enable
    public void testEnumArray() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setEnumArray(new Enum[]{SimpleEnumBean.C, null, SimpleEnumBean.B});

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getEnumField()).isEqualTo(expected.getEnumField());
    }

    @Test(enabled = false) //todo: enable
    public void testConcreteEnumArray() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setSimpleEnumBeanArray(new SimpleEnumBean[]{SimpleEnumBean.A, null, SimpleEnumBean.C});

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getEnumField()).isEqualTo(expected.getEnumField());
    }

    @Test(enabled = false) //todo: enable
    public void testObject() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setObjectField(SimpleEnumBean.B);

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getObjectField()).isEqualTo(expected.getObjectField());
    }

    @Test(enabled = false) //todo: enable
    public void testObjectArray() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setObjectArray(new Object[]{null, SimpleEnumBean.B, "Hello!"});

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getObjectArray()).isEqualTo(expected.getObjectArray());
    }

    @Test(enabled = false) //todo: enable
    public void testList() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setList(new ArrayList());
        expected.getList().addAll(Arrays.asList("Hi", null, SimpleEnumBean.B));

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getList()).isEqualTo(expected.getList());
    }
}

