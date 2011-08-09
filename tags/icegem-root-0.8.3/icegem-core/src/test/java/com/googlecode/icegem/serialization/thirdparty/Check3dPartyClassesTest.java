package com.googlecode.icegem.serialization.thirdparty;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import javassist.CannotCompileException;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;

import java.io.InvalidClassException;

/**
 * Test for checking serialization of DateTime class
 *
 * User: akondratyev
 * @author Andrey Stepanov aka standy
 */
public class Check3dPartyClassesTest extends TestParent{
    @BeforeTest
    public void setUp() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), JodaTime.class);
    }

    @Test
    public void jodaTime() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("UTC");
        Chronology chronology = ISOChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertThat(stored.getDateTime()).as("Date time object was not stored correctly").isEqualTo(o.getDateTime());
    }
    
    @Test
    public void jodaTimeNonStandardChrono() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Moscow");
        Chronology chronology = JulianChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertThat(stored.getDateTime()).as("Date time object was not stored correctly").isEqualTo(o.getDateTime());
    }


    @Test
    public void jodaTimeNonStandardTZ() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Berlin");
        Chronology chronology = ISOChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertThat(stored.getDateTime()).as("Date time object was not stored correctly").isEqualTo(o.getDateTime());
    }

    @Test
    public void jodaTimeNonStandardTZandChrono() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Berlin");
        Chronology chronology = BuddhistChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertThat(stored.getDateTime()).as("Date time object was not stored correctly").isEqualTo(o.getDateTime());
    }

}