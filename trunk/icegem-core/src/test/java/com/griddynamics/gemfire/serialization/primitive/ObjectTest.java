package com.griddynamics.gemfire.serialization.primitive;

import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.codegen.MethodFrameCounter;
import javassist.CannotCompileException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author igolovach
 */

public class ObjectTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), ObjectBean.class);
    }

    @Test
    public void testNull() {
        // create test bean
        ObjectBean expected = new ObjectBean();

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObj()).isNull();
    }

    @Test(expectedExceptions = RuntimeException.class) //NotSerializableException wrapped in RuntimeException by TestParent
    public void testObject() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj(new Object());

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObj()).isNull();
    }

    @Test
    public void testString() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj("Hello!");

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObj()).isInstanceOf(String.class);
        assertThat(actual.getObj()).isEqualTo(expected.getObj());
    }

    @Test
    public void testInteger(){
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj(123456);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObj()).isInstanceOf(Integer.class);
        assertThat(actual.getObj()).isEqualTo(expected.getObj());
    }

    @Test(expectedExceptions = StackOverflowError.class, expectedExceptionsMessageRegExp = MethodFrameCounter.MSG)
    public void testCycle1() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        expected.setObj(expected);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);
    }    

    @Test(expectedExceptions = StackOverflowError.class, expectedExceptionsMessageRegExp = MethodFrameCounter.MSG)
    public void testCycle2() {
        // create test bean
        ObjectBean expectedA = new ObjectBean();
        ObjectBean expectedB = new ObjectBean();
        expectedA.setObj(expectedB);
        expectedB.setObj(expectedA);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expectedA);
    }

    @Test
    public void testSequence(){
        // create test bean
        ObjectBean expectedA = new ObjectBean();
        ObjectBean expectedB = new ObjectBean();
        ObjectBean expectedC = new ObjectBean();
        expectedA.setObj(expectedB);
        expectedB.setObj(expectedC);
        expectedC.setObj(null);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expectedA);

        // assert
        assertThat(actual.getObj()).isInstanceOf(ObjectBean.class);
        assertThat(((ObjectBean) actual.getObj()).getObj()).isInstanceOf(ObjectBean.class);
        assertThat(((ObjectBean) ((ObjectBean) actual.getObj()).getObj()).getObj()).isNull();
    }

    @Test
    public void testObjectArrayZeroLength() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        Object[] expectedArr = new Object[0];
        expected.setObj(expectedArr);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObj()).isInstanceOf(Object[].class);
        assertThat(((Object[]) actual.getObj())).hasSize(expectedArr.length);
    }

    @Test
    public void testObjectArrayWithNulls() {
        // create test bean
        ObjectBean expected = new ObjectBean();
        Object[] expectedArr = new Object[]{null, null, null};
        expected.setObj(expectedArr);

        // Serialize / Deserialize
        ObjectBean actual = (ObjectBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getObj()).isInstanceOf(Object[].class);
        assertThat(((Object[]) actual.getObj())).hasSize(expectedArr.length);
        assertThat(((Object[]) actual.getObj())).isEqualTo(expectedArr);
    }
}

