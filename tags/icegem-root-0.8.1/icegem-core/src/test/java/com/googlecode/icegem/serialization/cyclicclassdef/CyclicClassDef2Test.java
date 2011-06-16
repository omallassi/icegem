package com.googlecode.icegem.serialization.cyclicclassdef;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;

import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author igolovach
 */

public class CyclicClassDef2Test extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), CyclicClassDef2BeanA.class, CyclicClassDef2BeanB.class);
    }

    @Test
    public void testWithNullField() {
        // create test bean
        CyclicClassDef2BeanA expected = new CyclicClassDef2BeanA();

        // Serialize / Deserialize
        CyclicClassDef2BeanA actual = (CyclicClassDef2BeanA) serializeAndDeserialize(expected);

        // assert Company correct
        Assertions.assertThat(actual.getData()).isEqualTo(expected.getData());
        Assertions.assertThat(actual.getNext()).isEqualTo(null);
    }

    @Test
    public void test() {
        // create test bean
        CyclicClassDef2BeanA expectedA0 = new CyclicClassDef2BeanA();
        CyclicClassDef2BeanB expectedB0 = new CyclicClassDef2BeanB();
        CyclicClassDef2BeanA expectedA1 = new CyclicClassDef2BeanA();
        CyclicClassDef2BeanB expectedB1 = new CyclicClassDef2BeanB();
        expectedA0.setData(111);
        expectedA1.setData(222);
        expectedB0.setData(333);
        expectedB1.setData(444);
        expectedA0.setNext(expectedB0);
        expectedB0.setNext(expectedA1);
        expectedA1.setNext(expectedB1);

        // Serialize / Deserialize
        CyclicClassDef2BeanA actualA0 = (CyclicClassDef2BeanA) serializeAndDeserialize(expectedA0);
        CyclicClassDef2BeanB actualB0 = actualA0.getNext();
        CyclicClassDef2BeanA actualA1 = actualB0.getNext();
        CyclicClassDef2BeanB actualB1 = actualA1.getNext();

        // assert Company correct
        Assertions.assertThat(actualA0.getData()).isEqualTo(expectedA0.getData());
        Assertions.assertThat(actualA1.getData()).isEqualTo(expectedA1.getData());
        Assertions.assertThat(actualB0.getData()).isEqualTo(expectedB0.getData());
        Assertions.assertThat(actualB1.getData()).isEqualTo(expectedB1.getData());
        Assertions.assertThat(actualA0.getNext()).isEqualTo(actualB0);
        Assertions.assertThat(actualA1.getNext()).isEqualTo(actualB1);
        Assertions.assertThat(actualB0.getNext()).isEqualTo(actualA1);
        Assertions.assertThat(actualB1.getNext()).isEqualTo(null);
    }
}
