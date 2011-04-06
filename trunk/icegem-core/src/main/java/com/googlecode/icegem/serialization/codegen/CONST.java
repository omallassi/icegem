package com.googlecode.icegem.serialization.codegen;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

/**
 * TODO: write javadoc
 *
 * @author igolovach
 */

public class CONST {
    public static final Set<Class<?>> CLASSES;

    static {
        CLASSES = new HashSet<Class<?>>();
        //
        CLASSES.add(String.class);
        CLASSES.add(Date.class);
        //
        CLASSES.add(BigDecimal.class);
        CLASSES.add(BigInteger.class);        
        //
        CLASSES.add(Number.class);
        CLASSES.add(Object.class);
        //
        CLASSES.add(Calendar.class);
        CLASSES.add(Locale.class);
        CLASSES.add(TimeZone.class);
        CLASSES.add(GregorianCalendar.class);
        //
        CLASSES.add(Currency.class);
        CLASSES.add(UUID.class);
        CLASSES.add(URL.class);
        CLASSES.add(URI.class);
    }
}
