package com.googlecode.icegem.serialization._inheritance.serializedclass.parenconcrete;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;

import javassist.CannotCompileException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;

import static org.junit.Assert.*;
/**
 * @author igolovach
 */
public class ParentConcreteTest extends TestParent {

    @BeforeClass
    public static void beforeClass() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), Bean.class, ParentMarked.class, MarkedChildOfMarkedParent.class, MarkedChildOfNotMarkedParent.class);
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

        assertTrue(actual.getParentMarked() instanceof ParentMarked);
        assertEquals(actual.getParentMarked().getParentData(),expected.getParentMarked().getParentData());
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
        assertTrue(actual.getParentMarked() instanceof MarkedChildOfMarkedParent);
        // assert: parent data
        assertEquals((actual.getParentMarked().getParentData()),expected.getParentMarked().getParentData());
        // assert: child data
        assertEquals(((MarkedChildOfMarkedParent) actual.getParentMarked()).getChildData(),((MarkedChildOfMarkedParent) expected.getParentMarked()).getChildData());
    }

    @Test(expected = NotSerializableException.class)
    public void testParentMarked_ValueNotMarkedChild() throws IOException {
        final Bean expected = new Bean();
        expected.setParentMarked(new NotMarkedChildOfMarkedParent());

        // Serialize
        final ByteArrayOutputStream buff = new ByteArrayOutputStream();
        DataSerializer.writeObject(expected, new DataOutputStream(buff));
    }

    @Test(expected = NotSerializableException.class)
    public void testParentNotMarked_ValueParent() throws IOException {
        final Bean expected = new Bean();
        expected.setParentNotMarked(new ParentNotMarked());


        // Serialize
        final ByteArrayOutputStream buff = new ByteArrayOutputStream();
        DataSerializer.writeObject(expected, new DataOutputStream(buff));

//        // Serialize / Deserialize
//        Bean actual = serializeAndDeserialize(expected);
//        int x = 0;
    }

    @Test
    public void testParentNotMarked_ValueMarkedChild() {
        final Bean expected = new Bean();
        expected.setParentNotMarked(new MarkedChildOfNotMarkedParent());
        expected.getParentNotMarked().setParentData(123);
        ((MarkedChildOfNotMarkedParent) expected.getParentNotMarked()).setChildData(654);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        assertTrue(actual.getParentNotMarked() instanceof MarkedChildOfNotMarkedParent);
        // assert: parent data
        assertEquals((actual.getParentNotMarked().getParentData()),expected.getParentNotMarked().getParentData());
        // assert: child data
        assertEquals(((MarkedChildOfNotMarkedParent) actual.getParentNotMarked()).getChildData(),((MarkedChildOfNotMarkedParent) expected.getParentNotMarked()).getChildData());
    }
}

