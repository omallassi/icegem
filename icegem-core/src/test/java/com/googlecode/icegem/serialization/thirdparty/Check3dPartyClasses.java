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
 * Created by IntelliJ IDEA.
 * User: volcano
 * Date: 4/7/11
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Check3dPartyClasses extends TestParent{

    @BeforeTest
    public void setUp() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Check3dPartyClasses.class.getClassLoader(), JodaTime.class);
    }

    @Test
    public void jodaTime() {
        JodaTime o = new JodaTime();
        o.setDateTime(new DateTime());
        System.out.println(o.getDateTime());

        JodaTime expected = serializeAndDeserialize(o);
        System.out.println("date: " + expected.getDateTime());
        assertThat(expected.getDateTime().equals(o.getDateTime()));
    }
}
