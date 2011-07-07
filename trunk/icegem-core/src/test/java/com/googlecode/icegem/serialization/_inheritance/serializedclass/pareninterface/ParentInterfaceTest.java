package com.googlecode.icegem.serialization._inheritance.serializedclass.pareninterface;

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
public class ParentInterfaceTest extends TestParent {

    @BeforeClass
    public static void beforeClass() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), Bean.class, MarkedChildOfNotMarkedParent.class);
    }

    @Test
    public void testEmpty() {
        final Bean expected = new Bean();

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);
    }

    @Test
    public void testParentNotMarked_ValueMarkedChild() {
        final Bean expected = new Bean();
        expected.setParentNotMarked(new MarkedChildOfNotMarkedParent());
        ((MarkedChildOfNotMarkedParent) expected.getParentNotMarked()).setChildData(654);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        assertTrue(actual.getParentNotMarked() instanceof MarkedChildOfNotMarkedParent);
        // assert: child data
        assertEquals(((MarkedChildOfNotMarkedParent) actual.getParentNotMarked()).getChildData(), ((MarkedChildOfNotMarkedParent) expected.getParentNotMarked()).getChildData());
    }
}

