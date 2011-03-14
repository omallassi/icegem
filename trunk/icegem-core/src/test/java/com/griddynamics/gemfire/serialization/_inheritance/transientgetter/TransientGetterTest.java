package com.griddynamics.gemfire.serialization._inheritance.transientgetter;

import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.primitive.TestParent;
import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author igolovach
 */

public class TransientGetterTest extends TestParent {

    @BeforeClass
    public void beforeClass() throws InvalidClassException, CannotCompileException {
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
        Assertions.assertThat(actual.getMarkedParent()).isInstanceOf(MarkedChildOfMarkedParent.class);
        // assert: data
        Assertions.assertThat((actual.getMarkedParent().getData())).isEqualTo(0);
    }

    @Test(enabled = false) //todo: enable
    public void testMarkedParentNotMarkedChild() {
        final Bean expected = new Bean();
        expected.setMarkedParent(new NotMarkedChildOfMarkedParent());
        expected.getMarkedParent().setData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        Assertions.assertThat(actual.getMarkedParent()).isInstanceOf(NotMarkedChildOfMarkedParent.class);
        // assert: data
        Assertions.assertThat((actual.getMarkedParent().getData())).isEqualTo(expected.getMarkedParent().getData());
    }

    @Test(enabled = false) //todo: enable
    public void testNotMarkedParentMarkedChild() {
        final Bean expected = new Bean();
        expected.setNotMarkedParent(new MarkedChildOfNotMarkedParent());
        expected.getNotMarkedParent().setData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        Assertions.assertThat(actual.getNotMarkedParent()).isInstanceOf(MarkedChildOfNotMarkedParent.class);
        // assert: data
        Assertions.assertThat((actual.getNotMarkedParent().getData())).isEqualTo(0);
    }

    @Test
    public void testNotMarkedParentNotMarkedChild() {
        final Bean expected = new Bean();
        expected.setNotMarkedParent(new NotMarkedChildOfNotMarkedParent());
        expected.getNotMarkedParent().setData(123);

        // Serialize / Deserialize
        Bean actual = serializeAndDeserialize(expected);

        // assert: type
        Assertions.assertThat(actual.getNotMarkedParent()).isInstanceOf(NotMarkedChildOfNotMarkedParent.class);
        // assert: data
        Assertions.assertThat((actual.getNotMarkedParent().getData())).isEqualTo(expected.getNotMarkedParent().getData());
    }
}

//todo: remove
//class D {
//    public static void main(String[] args) throws IOException {
//        Object obj = null;
//
//        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        DataSerializer.writeObject(obj, new DataOutputStream(baos), false);
//        byte[] data = baos.toByteArray();
//        System.out.println(data.length);
//        System.out.println(new String(data));
//    }
//}
