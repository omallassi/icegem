package com.googlecode.icegem.serialization.thirdparty;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import static org.junit.Assert.*;

/**
 * Test for checking serialization of DateTime class
 *
 * User: akondratyev
 * @author Andrey Stepanov aka standy
 */
public class Check3dPartyClassesTest extends TestParent{
    @BeforeClass
    public static void register() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), JodaTime.class);
    }

    @Test
    public void jodaTime() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("UTC");
        Chronology chronology = ISOChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertEquals(stored.getDateTime(), o.getDateTime());
    }
    
    @Test
    public void jodaTimeNonStandardChrono() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Moscow");
        Chronology chronology = JulianChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertEquals(stored.getDateTime(), o.getDateTime());
    }


    @Test
    public void jodaTimeNonStandardTZ() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Berlin");
        Chronology chronology = ISOChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertEquals(stored.getDateTime(), o.getDateTime());
    }

    @Test
    public void jodaTimeNonStandardTZandChrono() {
        JodaTime o = new JodaTime();
        DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Berlin");
        Chronology chronology = BuddhistChronology.getInstance(dateTimeZone);
        DateTime dateTime = new DateTime(System.currentTimeMillis(), chronology);
        o.setDateTime(dateTime);

        JodaTime stored = serializeAndDeserialize(o);
        assertEquals(stored.getDateTime(), o.getDateTime());
    }

}
