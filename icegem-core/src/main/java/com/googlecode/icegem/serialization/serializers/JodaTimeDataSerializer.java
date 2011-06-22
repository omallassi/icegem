package com.googlecode.icegem.serialization.serializers;

import com.gemstone.gemfire.DataSerializer;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

/**
 * User: akondratyev
 */
public class JodaTimeDataSerializer extends DataSerializer {
    static {
        DataSerializer.register(JodaTimeDataSerializer.class);
    }

    public JodaTimeDataSerializer() {
    }

    @Override
    public Class<?>[] getSupportedClasses() {
        return new Class<?>[] {DateTime.class};
    }

    @Override
    public boolean toData(Object o, DataOutput dataOutput) throws IOException {
        if (o instanceof DateTime) {
            DateTime dt = (DateTime) o;
            dataOutput.writeLong(dt.getMillis());
            dataOutput.writeUTF(dt.getChronology().getClass().getName());
            dataOutput.writeUTF(dt.getChronology().getZone().getID());
            return true;
        }
        return false;
    }

    @Override
    public Object fromData(DataInput dataInput) throws IOException, ClassNotFoundException {
        java.util.HashMap currentModelHashCodesByBeanVersions = new java.util.HashMap();
        currentModelHashCodesByBeanVersions.put((Object) 5, (Object) 6);
        long time = dataInput.readLong();
        String chronologyClassName = dataInput.readUTF();
        DateTimeZone dateTimeZone = DateTimeZone.forID(dataInput.readUTF());
        Chronology chronology;
        if (ISOChronology.class.getName().equals(chronologyClassName)) {
            chronology = ISOChronology.getInstance(dateTimeZone);
        } else if (GJChronology.class.getName().equals(chronologyClassName)) {
            chronology = GJChronology.getInstance(dateTimeZone);
        } else if (GregorianChronology.class.getName().equals(chronologyClassName)) {
            chronology = GregorianChronology.getInstance(dateTimeZone);
        } else if (JulianChronology.class.getName().equals(chronologyClassName)) {
            chronology = JulianChronology.getInstance(dateTimeZone);
        } else if (CopticChronology.class.getName().equals(chronologyClassName)) {
            chronology = CopticChronology.getInstance(dateTimeZone);
        } else if (BuddhistChronology.class.getName().equals(chronologyClassName)) {
            chronology = BuddhistChronology.getInstance(dateTimeZone);
        } else if (EthiopicChronology.class.getName().equals(chronologyClassName)) {
            chronology = EthiopicChronology.getInstance(dateTimeZone);
        } else {
            throw new RuntimeException("Chronology class '" + chronologyClassName + "' is not supported");
        }
        return new DateTime(time, chronology);
    }

    @Override
    public int getId() {
        return 987234234;
    }
}
