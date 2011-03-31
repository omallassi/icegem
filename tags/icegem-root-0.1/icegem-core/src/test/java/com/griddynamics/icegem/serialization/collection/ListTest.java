package com.griddynamics.icegem.serialization.collection;

import com.griddynamics.icegem.serialization.HierarchyRegistry;
import com.griddynamics.icegem.serialization.primitive.TestParent;

import javassist.CannotCompileException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author igolovach
 */

public class ListTest extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), ListBean.class);
    }

    @Test
    public void testNull() {
        // create test bean
        ListBean expected = new ListBean();

        // Serialize / Deserialize
        ListBean actual = (ListBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getArrayList()).isNull();
        assertThat(actual.getLinkedList()).isNull();
        assertThat(actual.getList()).isNull();
    }

    @Test
    public void testEmpty() {
        // create test bean
        ListBean expected = new ListBean();
        expected.setArrayList(new ArrayList());
        expected.setLinkedList(new LinkedList());
        expected.setList(new ArrayList());

        // Serialize / Deserialize
        ListBean actual = (ListBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getArrayList()).hasSize(0);
        assertThat(actual.getLinkedList()).hasSize(0);

        assertThat((Object) actual.getList()).isInstanceOf(expected.getList().getClass());
        assertThat(actual.getList()).hasSize(0);
    }

    @Test
    public void testWithData() {
        // create test bean
        ListBean expected = new ListBean();
        expected.setArrayList(new ArrayList());
        expected.getArrayList().add("Hello");
        expected.getArrayList().add(new Date());
        expected.setLinkedList(new LinkedList());
        expected.getArrayList().add("Hello-2");
        expected.getArrayList().add(new Date(123456789));
        expected.setList(new LinkedList());
        expected.getArrayList().add("Hello-3");
//        expected.getArrayList().add(Locale.US); //todo: uncomment

        // Serialize / Deserialize
        ListBean actual = (ListBean) serializeAndDeserialize(expected);

        // assert
        assertThat(actual.getArrayList()).isEqualTo(expected.getArrayList());
        assertThat(actual.getLinkedList()).isEqualTo(expected.getLinkedList());
        assertThat(actual.getList()).isEqualTo(expected.getList());
    }

    @Test(enabled = false)
    //todo: test "new ArrayList/LinkedList/List() {}"
    //todo: test Locale
    public void testArrayListSubclass() {
        // create test bean
        ListBean expected = new ListBean();
        expected.setArrayList(new ArrayList() {
        });
        expected.getArrayList().add("Hello");
        expected.getArrayList().add(new Date() {
        });
        expected.setLinkedList(new LinkedList());
        expected.getArrayList().add("Hello-2");
        expected.getArrayList().add(new Date(123456789));
        expected.setList(new LinkedList() {
        });
        expected.getArrayList().add("Hello-3");
        expected.getArrayList().add(Locale.US);

        // Serialize / Deserialize
        ListBean actual = (ListBean) serializeAndDeserialize(expected);

        // assert
        assertThat((Object) actual.getArrayList()).isInstanceOf(ArrayList.class);
        assertThat(actual.getArrayList()).isEqualTo(expected.getArrayList());
    }
}

