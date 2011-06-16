package com.googlecode.icegem.serialization.thirdparty;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import javassist.CannotCompileException;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;

import java.io.InvalidClassException;

/**
 * User: akondratyev
 */
public class Check3dPartyClassesTest extends TestParent{

    @BeforeTest
    public void setUp() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), JodaTime.class);
    }

    @Test
    public void jodaTime() {
        JodaTime o = new JodaTime();
        o.setDateTime(new DateTime());

        JodaTime expected = serializeAndDeserialize(o);
        assertThat(expected.getDateTime().equals(o.getDateTime()));
    }
}
