package com.googlecode.icegem.serialization._jdktypes;

import java.io.InvalidClassException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javassist.CannotCompileException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.gemstone.bp.edu.emory.mathcs.backport.java.util.Arrays;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import static org.junit.Assert.*;

/**
 * @author igolovach
 */
@RunWith(Parameterized.class)
public class JdkDateTest extends TestParent { 

	Date date;
	
	public JdkDateTest(Date date) {
		this.date = date;
	}
	
    @BeforeClass
    public static void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), _JdkTypesBean.class);
    }

    @Parameters
    public static Collection<Object[]> dataDate() {
        return Arrays.asList(new Object[][]{
                new Object[]{new Date(+123456789L)}, 
                new Object[]{new Date(0L)},
                new Object[]{new Date(-123456789L)},
                // DataSerializer doesn't serialize Date(-1) ('-1' used as null-flag)
                new Object[]{new Date(-1L)},
        });
    }

    @Test
    public void testDate() {
        // create test bean
        _JdkTypesBean expected = new _JdkTypesBean();
        expected.setDate(date);

        // Serialize / Deserialize
        _JdkTypesBean actual = serializeAndDeserialize(expected);

        // assert
        assertEquals(actual.getDate(), expected.getDate());
    }
}

