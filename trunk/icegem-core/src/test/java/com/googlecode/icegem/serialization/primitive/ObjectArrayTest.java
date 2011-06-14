package com.googlecode.icegem.serialization.primitive;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;

import com.googlecode.icegem.serialization.codegen.exception.MethodFrameStackOverflowException;
import javassist.CannotCompileException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author igolovach
 */

public class ObjectArrayTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), ObjectArrayBean.class);
    }

    @Test
    public void testNull() {
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObjArr()).isNull();
    }

    @Test(expectedExceptions = RuntimeException.class) //NotSerializableException wrapped in RuntimeException by TestParent
    public void testObject(){
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        expected.setObjArr(new Object[]{new Object()});

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expected);
    }

    @Test
    public void testString(){
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        final Object[] expectedArr = {null, "Hello!"};
        expected.setObjArr(expectedArr);

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObjArr()).hasSize(expectedArr.length);
    }

    @Test(expectedExceptions = StackOverflowError.class)
    public void testCycle1() {
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        expected.setObjArr(new Object[]{expected});

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expected);
    }

    @Test(expectedExceptions = StackOverflowError.class)
    public void testCycle2() {
        // create test bean
        ObjectArrayBean expectedA = new ObjectArrayBean();
        ObjectArrayBean expectedB = new ObjectArrayBean();
        expectedA.setObjArr(new Object[]{expectedB});
        expectedB.setObjArr(new Object[]{expectedA});

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expectedA);
    }

    @Test
    public void testSequence(){
        // create test bean
        ObjectArrayBean expectedA = new ObjectArrayBean();
        ObjectArrayBean expectedB = new ObjectArrayBean();
        ObjectArrayBean expectedC = new ObjectArrayBean();
        expectedA.setObjArr(new Object[]{"Hi!", null, 123, expectedB});
        expectedB.setObjArr(new Object[]{expectedC, expectedC, expectedC});
        expectedC.setObjArr(new Object[0]);

        // Serialize / Deserialize
        ObjectArrayBean actualA = (ObjectArrayBean) serializeAndDeserialize(expectedA);

        // assert
        assertThat(actualA.getObjArr()[0]).isEqualTo(expectedA.getObjArr()[0]);
        assertThat(actualA.getObjArr()[1]).isEqualTo(expectedA.getObjArr()[1]);
        assertThat(actualA.getObjArr()[2]).isEqualTo(expectedA.getObjArr()[2]);
        ObjectArrayBean actualB = (ObjectArrayBean) actualA.getObjArr()[3];
        ObjectArrayBean actualC0 = (ObjectArrayBean) actualB.getObjArr()[0];
        ObjectArrayBean actualC1 = (ObjectArrayBean) actualB.getObjArr()[1];
        ObjectArrayBean actualC2 = (ObjectArrayBean) actualB.getObjArr()[2];
        assertThat(actualC0.getObjArr()).isEqualTo(expectedC.getObjArr());
        assertThat(actualC1.getObjArr()).isEqualTo(expectedC.getObjArr());
        assertThat(actualC2.getObjArr()).isEqualTo(expectedC.getObjArr());
    }

    @Test
    public void testObjectArrayZeroLength() {
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        Object[] expectedArr = new Object[0];
        expected.setObjArr(expectedArr);

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObjArr()).hasSize(expectedArr.length);
    }

    @Test
    public void testObjectArrayWithNulls() {
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        Object[] expectedArr = new Object[]{null, null, null};
        expected.setObjArr(expectedArr);

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObjArr()).isEqualTo(expectedArr);
    }

    @Test
    public void testManyDimensionalObjectArray() {
        // create test bean
        ObjectArrayBean expected = new ObjectArrayBean();
        Object[] expectedArr = new Object[][][]{
                new Object[][]{
                        new Object[]{"0", "1"}, new Object[]{"a", "b"}
                },
                new Object[][]{
                        new Object[]{"2", "3"}, new Object[]{"c", "d"}
                },
                new Object[][]{
                        new Object[]{"4", "5"}, new Object[]{"e", "f"}
                },
                new Object[][]{
                        new Object[]{"6", "7"}, new Object[]{"g", "h"}
                }
        };
        expected.setObjArr(expectedArr);

        // Serialize / Deserialize
        ObjectArrayBean actual = (ObjectArrayBean) serializeAndDeserialize(expected);

        // assert
        assertThat((Object)actual.getObjArr()).isInstanceOf(Object[][][].class);
        Object[][][] actualArr = (Object[][][])actual.getObjArr();
        assertThat(actualArr).isEqualTo(expectedArr);
    }
}

