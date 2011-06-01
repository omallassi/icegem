package com.googlecode.icegem.serialization._jdktypes;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;

import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.*;

import java.io.InvalidClassException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author igolovach
 */

public class AllowedJdkTypesTest extends TestParent { //todo: what if field of type sql.Date/Time/Timestamp ?

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), _JdkTypesBean.class);
    }

    @DataProvider(name = "calendar")
    public Object[][] dataCalendar() {
        //todo: more strange calendars?
        Calendar c0 = Calendar.getInstance();
        c0.setTimeInMillis(1234567890L);
        Calendar c1 = Calendar.getInstance(new Locale("th", "TH")); //todo: killer calendar
        c1.setTimeInMillis(1234567890L);
        Calendar c2 = Calendar.getInstance(new Locale("ja", "JP", "JP")); //todo: killer calendar
        c2.setTimeInMillis(1234567890L);

        return new Object[][]{
                new Object[]{c0},
                new Object[]{c1},
                new Object[]{c2},
        };
    }

    @Test(dataProvider = "calendar", enabled = false) //todo: enable
    public void testCalendar(Calendar calendar) {
        // create test bean
        _JdkTypesBean expected = new _JdkTypesBean();
        expected.setCalendar(calendar);

        // Serialize / Deserialize
        _JdkTypesBean actual = serializeAndDeserialize(expected);

        // assert
        Assertions.assertThat(actual.getCalendar()).isEqualTo(expected.getCalendar());
    }

    /**
     * Read about <a href="DataInput.html#modified-utf-8">modified UTF-8</a> format.
     */
    @DataProvider(name = "string")
    public Object[][] dataString() {
        return new Object[][]{
                // simple
                new Object[]{""},
                new Object[]{" "},
                new Object[]{"Hello World!"},
                // isolate 2 byte '0' in modified UTF-8
                new Object[]{nCopy('\u0000', 1)},
                new Object[]{nCopy('\u0000', 100)},
                new Object[]{nCopy('\u0000', 10000)},
                new Object[]{nCopy('\u0000', 100000)},
                // minimal 1-byte in modified UTF-8
                new Object[]{nCopy('\u0001', 1)},
                new Object[]{nCopy('\u0001', 100)},
                new Object[]{nCopy('\u0001', 10000)},
                new Object[]{nCopy('\u0001', 100000)},
                // maximal 1-byte in modified UTF-8
                new Object[]{nCopy('\u007F', 1)},
                new Object[]{nCopy('\u007F', 100)},
                new Object[]{nCopy('\u007F', 10000)},
                new Object[]{nCopy('\u007F', 100000)},
                // minimal 2-byte in modified UTF-8
                new Object[]{nCopy('\u0080', 1)},
                new Object[]{nCopy('\u0080', 100)},
                new Object[]{nCopy('\u0080', 10000)},
                new Object[]{nCopy('\u0080', 100000)},
                // maximal 2-byte in modified UTF-8
                new Object[]{nCopy('\u07FF', 1)},
                new Object[]{nCopy('\u07FF', 100)},
                new Object[]{nCopy('\u07FF', 10000)},
                new Object[]{nCopy('\u07FF', 100000)},
                // minimal 3-byte in modified UTF-8
                new Object[]{nCopy('\u0800', 1)},
                new Object[]{nCopy('\u0800', 100)},
                new Object[]{nCopy('\u0800', 10000)},
                new Object[]{nCopy('\u0800', 100000)},
                // maximal 3-byte in modified UTF-8
                new Object[]{nCopy('\uFFFF', 1)},
                new Object[]{nCopy('\uFFFF', 100)},
                new Object[]{nCopy('\uFFFF', 10000)},
                new Object[]{nCopy('\uFFFF', 100000)},
        };
    }
    
    @Test(dataProvider = "string")
    public void testString(String string) {
        // create test bean
        _JdkTypesBean expected = new _JdkTypesBean();
        expected.setString(string);

        // Serialize / Deserialize
        _JdkTypesBean actual = serializeAndDeserialize(expected);

        // assert
        Assertions.assertThat(actual.getString()).isEqualTo(expected.getString());
    }

    @DataProvider(name = "date")
    public Object[][] dataDate() {
        return new Object[][]{
                new Object[]{new Date(+123456789L)}, //todo: what about 'specific' numbers? Does exist such for Date?
                new Object[]{new Date(0L)},
                new Object[]{new Date(-123456789L)},
                // DataSerializer doesn't serialize Date(-1) ('-1' used as null-flag)
                new Object[]{new Date(-1L)},
        };
    }

    @DataProvider(name = "date -> exception")
    public Object[][] dataDateWithException() {
        return new Object[][]{
                // JDK child of java.util.Date
                new Object[]{new java.sql.Date(123456789L)},
                new Object[]{new java.sql.Time(123456789L)},
                new Object[]{new java.sql.Timestamp(123456789L)},
                // custom child of java.util.Date
                new Object[]{new Date(123456789L) {
                }},
        };
    }

    @Test(dataProvider = "date")
    public void testDate(Date date) {
        // create test bean
        _JdkTypesBean expected = new _JdkTypesBean();
        expected.setDate(date);

        // Serialize / Deserialize
        _JdkTypesBean actual = serializeAndDeserialize(expected);

        // assert
        Assertions.assertThat(actual.getDate()).isEqualTo(expected.getDate());
    }

    @Test(dataProvider = "date -> exception", expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = TestParent.MSG)
    public void testDateWithException(Date date) {
        testDate(date);
    }

    private static String nCopy(char c, int n) { //todo: slow
        StringBuilder buff = new StringBuilder(n);
        for (int k = 0; k < n; k++) {
            buff.append(c);
        }
        return buff.toString();
    }
}

