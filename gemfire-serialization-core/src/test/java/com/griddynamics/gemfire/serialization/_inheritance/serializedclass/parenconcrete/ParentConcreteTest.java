package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parenconcrete;

import com.gemstone.gemfire.DataSerializer;
import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.TestParent;
import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;

/**
 * @author igolovach
 */
public class ParentConcreteTest extends TestParent {

    @BeforeClass
    public void beforeClass() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), Bean.class, ParentMarked.class, MarkedChildOfMarkedParent.class, MarkedChildOfNotMarkedParent.class);
    }

    @Test
    public void testEmpty() {
        final Bean expected = new Bean();

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);
    }

    @Test
    public void testParentMarked_ValueParent() {
        final Bean expected = new Bean();
        expected.setParentMarked(new ParentMarked());
        expected.getParentMarked().setParentData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        Assertions.assertThat(actual.getParentMarked()).isInstanceOf(ParentMarked.class);
        Assertions.assertThat(actual.getParentMarked().getParentData()).isEqualTo(expected.getParentMarked().getParentData());
    }

    @Test
    public void testParentMarked_ValueMarkedChild() {
        final Bean expected = new Bean();
        expected.setParentMarked(new MarkedChildOfMarkedParent());
        expected.getParentMarked().setParentData(543);
        ((MarkedChildOfMarkedParent) expected.getParentMarked()).setChildData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        Assertions.assertThat(actual.getParentMarked()).isInstanceOf(MarkedChildOfMarkedParent.class);
        // assert: parent data
        Assertions.assertThat((actual.getParentMarked().getParentData())).isEqualTo(expected.getParentMarked().getParentData());
        // assert: child data
        Assertions.assertThat(((MarkedChildOfMarkedParent) actual.getParentMarked()).getChildData()).isEqualTo(((MarkedChildOfMarkedParent) expected.getParentMarked()).getChildData());
    }

    @Test(expectedExceptions = NotSerializableException.class)
    public void testParentMarked_ValueNotMarkedChild() throws IOException {
        final Bean expected = new Bean();
        expected.setParentMarked(new NotMarkedChildOfMarkedParent());

        // Serialize
        final ByteArrayOutputStream buff = new ByteArrayOutputStream();
        DataSerializer.writeObject(expected, new DataOutputStream(buff));
    }

    @Test(expectedExceptions = NotSerializableException.class, enabled = false) //todo: uncomment
    public void testParentNotMarked_ValueParent() throws IOException {
        final Bean expected = new Bean();
        expected.setParentNotmarked(new ParentNotMarked());

        // Serialize
        final ByteArrayOutputStream buff = new ByteArrayOutputStream();
        DataSerializer.writeObject(expected, new DataOutputStream(buff));

//        // Serialize / Deserialize
//        Bean actual = serializeAndDeserialize(expected);
//        int x = 0;
    }

    @Test(enabled = false) //todo: uncomment
    public void testParentNotMarked_ValueMarkedChild() {
        final Bean expected = new Bean();
        expected.setParentNotmarked(new MarkedChildOfNotMarkedParent());
        expected.getParentNotmarked().setParentData(123);
        ((MarkedChildOfNotMarkedParent) expected.getParentNotmarked()).setChildData(654);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        Assertions.assertThat(actual.getParentNotmarked()).isInstanceOf(MarkedChildOfNotMarkedParent.class);
        // assert: parent data
        Assertions.assertThat((actual.getParentNotmarked().getParentData())).isEqualTo(expected.getParentNotmarked().getParentData());
        // assert: child data
        Assertions.assertThat(((MarkedChildOfNotMarkedParent) actual.getParentNotmarked()).getChildData()).isEqualTo(((MarkedChildOfNotMarkedParent) expected.getParentNotmarked()).getChildData());
    }
}

