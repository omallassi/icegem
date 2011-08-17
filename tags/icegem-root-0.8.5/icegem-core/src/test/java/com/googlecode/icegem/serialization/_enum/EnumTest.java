package com.googlecode.icegem.serialization._enum;

import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Arrays;

import javassist.CannotCompileException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import static org.junit.Assert.*;

/**
 * @author igolovach
 */
@Ignore
public class EnumTest extends TestParent {

    @BeforeClass
    public void beforeClass() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), FieldEnumBean.class, SimpleEnumBean.class, ExtendedFinalEnumBean.class, ExtendedMutableEnumBean.class);
    }

    @Test 
    public void testRootSimple() {
        final SimpleEnumBean expected = SimpleEnumBean.A;

        // Serialize / Deserialize
        SimpleEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual, expected);
    }

    @Test
    public void testRootExtendedFinal() {
        final ExtendedFinalEnumBean expected = ExtendedFinalEnumBean.X;

        // Serialize / Deserialize
        ExtendedFinalEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual,expected);
    }    

    @Test 
    public void testRootExtendedMutable() {
        final ExtendedMutableEnumBean expected = ExtendedMutableEnumBean.M;

        // Serialize / Deserialize
        ExtendedMutableEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual,expected);
    }

    @Test 
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

        assertEquals(actual.getSimpleEnumBean(),expected.getSimpleEnumBean());

        assertEquals(actual.getExtendedFinalEnumBean(),expected.getExtendedFinalEnumBean());
        assertEquals(actual.getExtendedFinalEnumBean().getFinalName(),expected.getExtendedFinalEnumBean().getFinalName());

        assertEquals(actual.getExtendedMutableEnumBean(),expected.getExtendedMutableEnumBean());
        assertEquals(actual.getExtendedMutableEnumBean().getMutableName(),expected.getExtendedMutableEnumBean().getMutableName());
    }

    @Test 
    public void testEnum() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setEnumField(SimpleEnumBean.B);

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual.getEnumField(),expected.getEnumField());
    }

    @Test 
    public void testEnumArray() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setEnumArray(new Enum[]{SimpleEnumBean.C, null, SimpleEnumBean.B});

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual.getEnumField(),expected.getEnumField());
    }

    @Test 
    public void testConcreteEnumArray() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setSimpleEnumBeanArray(new SimpleEnumBean[]{SimpleEnumBean.A, null, SimpleEnumBean.C});

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual.getEnumField(),expected.getEnumField());
    }

    @Test 
    public void testObject() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setObjectField(SimpleEnumBean.B);

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual.getObjectField(),expected.getObjectField());
    }

    @Test 
    public void testObjectArray() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setObjectArray(new Object[]{null, SimpleEnumBean.B, "Hello!"});

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual.getObjectArray(),expected.getObjectArray());
    }

    @Test 
    public void testList() {
        final FieldEnumBean expected = new FieldEnumBean();
        expected.setList(new ArrayList());
        expected.getList().addAll(Arrays.asList("Hi", null, SimpleEnumBean.B));

        // Serialize / Deserialize
        FieldEnumBean actual = serializeAndDeserialize(expected);

        assertEquals(actual.getList(),expected.getList());
    }
}

