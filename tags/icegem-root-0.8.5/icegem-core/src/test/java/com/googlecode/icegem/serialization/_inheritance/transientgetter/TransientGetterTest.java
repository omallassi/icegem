package com.googlecode.icegem.serialization._inheritance.transientgetter;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import static org.junit.Assert.*;

/**
 * @author igolovach
 */

public class TransientGetterTest extends TestParent {

    @BeforeClass
    public static void beforeClass() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), Bean.class, MarkedChildOfMarkedParent.class, MarkedChildOfNotMarkedParent.class, NotMarkedChildOfMarkedParent.class, NotMarkedChildOfNotMarkedParent.class);
    }

    @Test
    public void testEmpty() {
        final Bean expected = new Bean();

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);
    }

    @Test
    public void testMarkedParentMarkedChild() {
        final Bean expected = new Bean();
        expected.setMarkedParent(new MarkedChildOfMarkedParent());
        expected.getMarkedParent().setData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        assertTrue(actual.getMarkedParent() instanceof MarkedChildOfMarkedParent);
        // assert: data
        assertEquals((actual.getMarkedParent().getData()), 0);
    }

    @Test
    public void testMarkedParentNotMarkedChild() {
        final Bean expected = new Bean();
        expected.setMarkedParent(new NotMarkedChildOfMarkedParent());
        expected.getMarkedParent().setData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        assertTrue(actual.getMarkedParent() instanceof NotMarkedChildOfMarkedParent);
        // assert: data
        assertEquals(actual.getMarkedParent().getData(), expected.getMarkedParent().getData());
    }

    @Test
    public void testNotMarkedParentMarkedChild() {
        final Bean expected = new Bean();
        expected.setNotMarkedParent(new MarkedChildOfNotMarkedParent());
        expected.getNotMarkedParent().setData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        assertTrue(actual.getNotMarkedParent() instanceof MarkedChildOfNotMarkedParent);
        // assert: data
        assertEquals(actual.getNotMarkedParent().getData(), 0);
    }

    @Test
    public void testNotMarkedParentNotMarkedChild() {
        final Bean expected = new Bean();
        expected.setNotMarkedParent(new NotMarkedChildOfNotMarkedParent());
        expected.getNotMarkedParent().setData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        assertTrue(actual.getNotMarkedParent() instanceof NotMarkedChildOfNotMarkedParent);
        // assert: data
        assertEquals(actual.getNotMarkedParent().getData(),expected.getNotMarkedParent().getData());
    }


    @Test
    public void testTransient() throws Exception{
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), ImplForInterfaceForTransient.class);

        Object obj = new ImplForInterfaceForTransient();
        serializeAndDeserialize(obj);
    }
}
