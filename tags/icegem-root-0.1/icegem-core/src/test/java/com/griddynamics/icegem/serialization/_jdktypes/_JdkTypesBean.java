package com.griddynamics.icegem.serialization._jdktypes;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 13)
@BeanVersion(1)
public class _JdkTypesBean {
    // common
    private String string;
    private CharSequence charSequence; //todo
    private StringBuffer stringBuffer; //todo
    private StringBuilder stringBuilder; //todo
    //
    private Thread thread; //todo
    private Void void_; //todo
    private Class clazz;
    private ClassLoader classLoader; //todo
    private InheritableThreadLocal inheritableThreadLocal; //todo
    private ThreadLocal threadLocal; //todo
    private Iterable iterable; //todo
    private Number number; //todo: ???
    private Object object; //todo:
    private Runnable runnable; //todo:
    // time/date
    private Date date;
    private Calendar calendar;
    private Locale locale;
    private TimeZone timeZone; //todo: SimpleTimeZone, ZoneInfo
    private GregorianCalendar gregorianCalendar;
    // math
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;
    // net
    private URL url;
    private URI uri;
    private Currency currency; //todo: ???
    private Enumeration enumeration;
    private Iterator iterator;
    private ResourceBundle resourceBundle;
    private UUID uuid;  


    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
