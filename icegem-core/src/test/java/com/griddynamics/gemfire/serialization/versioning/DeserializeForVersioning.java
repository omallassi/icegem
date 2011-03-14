package com.griddynamics.gemfire.serialization.versioning;

import com.gemstone.gemfire.DataSerializer;
import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.versioning.beans.beanv2.Company;
import com.griddynamics.gemfire.serialization.versioning.incorrect.v2.IllegalVersion;
import com.griddynamics.gemfire.serialization.versioning.inheritance.v2.Son;
import com.griddynamics.gemfire.serialization.versioning.manyVersions.v3.Car;
import javassist.CannotCompileException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;

import java.io.*;
import java.util.Arrays;

/**
 * User: akondratyev
 */
public class DeserializeForVersioning {
    @BeforeTest
    public void before() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(DeserializeForVersioning.class.getClassLoader(),
                Company.class, IllegalVersion.class, Son.class, Car.class);
    }
    @Test(enabled = true)
    public void deserializePreviousVersion() throws IOException, CannotCompileException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("simpleCompany.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("simpleCompany.versionTest"));
        in.readFully(buf);

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);

        Company c2 = DataSerializer.readObject(new DataInputStream(byteArray));
        assertThat(c2.getId()).isEqualTo(123);
        assertThat(c2.getName()).isNull();
    }

    @Test(expectedExceptions = ClassCastException.class, enabled = true)
    public void deserializeNewVersionWithOldCLass() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("restoreNewClassVersion.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("restoreNewClassVersion.versionTest"));
        in.readFully(buf);
        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);

        IllegalVersion illegalVersion = DataSerializer.readObject(new DataInputStream(byteArray));
    }

    @Test(enabled = true)
    public void deserializeWithInheritance() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("restoreNewClassVersion.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("restoreNewClassVersion.versionTest"));
        in.readFully(buf);
        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);

        Son son= DataSerializer.readObject(new DataInputStream(byteArray));
        assertThat(son.getId()).isEqualTo(0);
        assertThat(son.getName()).as("son's name");
        assertThat(son.getAge()).isEqualTo(23);
        assertThat(son.getSisters()).isNull();
        assertThat(son.getBurthday()).isNull();
        assertThat(son.getBrothers()).isEqualTo(Arrays.asList(4L, 3L, 5L, 1L));
    }

    @Test(enabled = true)
    public void deserializeCarVersionThree() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("cars.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("cars.versionTest"));
        in.readFully(buf);
        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);
        Car car = DataSerializer.readObject(new DataInputStream(byteArray));
        assertThat(car.getModel()).isEqualTo("golf");
        assertThat(car.getVersion()).isEqualTo("5");
        assertThat(car.getSeatCount()).isEqualTo(4);
        assertThat(car.isSedan()).isTrue();
    }


    @AfterTest
    public void deleteDataFile() {
        for(File file: new File(".").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.endsWith(".versionTest"))
                    return true;
                return false;
            }
        }))
            file.delete();
    }


}
