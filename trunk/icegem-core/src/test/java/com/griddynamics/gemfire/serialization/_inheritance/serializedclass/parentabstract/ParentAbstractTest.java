package com.griddynamics.gemfire.serialization._inheritance.serializedclass.parentabstract;

import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.TestParent;
import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.BeforeClass;

import java.io.InvalidClassException;

/**
 * @author igolovach
 */
public class ParentAbstractTest extends TestParent {

    @BeforeClass
    public void beforeClass() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), Bean.class, MarkedChildOfNotMarkedParent.class);
    }

    @org.testng.annotations.Test
    public void testEmpty() {
        final Bean expected = new Bean();

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);
    }

    @org.testng.annotations.Test
    public void testParentNotMarked_ValueMarkedChild() {
        final Bean expected = new Bean();
        expected.setParentNotMarked(new MarkedChildOfNotMarkedParent());
        expected.getParentNotMarked().setParentData(333);
        ((MarkedChildOfNotMarkedParent) expected.getParentNotMarked()).setChildData(654);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        Assertions.assertThat(actual.getParentNotMarked()).isInstanceOf(MarkedChildOfNotMarkedParent.class);
        // assert: parent data
        Assertions.assertThat((actual.getParentNotMarked()).getParentData()).isEqualTo((expected.getParentNotMarked()).getParentData());
        // assert: child data
        Assertions.assertThat(((MarkedChildOfNotMarkedParent) actual.getParentNotMarked()).getChildData()).isEqualTo(((MarkedChildOfNotMarkedParent) expected.getParentNotMarked()).getChildData());
    }
}

