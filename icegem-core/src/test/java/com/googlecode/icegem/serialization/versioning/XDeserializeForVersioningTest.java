package com.googlecode.icegem.serialization.versioning;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.versioning.beans.previousversion.beanv2.Company;
import com.googlecode.icegem.serialization.versioning.beans.modified.beanv2.Person;
import com.googlecode.icegem.serialization.versioning.beans.incorrect.v2.IllegalVersion;
import com.googlecode.icegem.serialization.versioning.beans.inheritance.v2.Son;
import com.googlecode.icegem.serialization.versioning.beans.manyVersions.v3.Car;

import com.googlecode.icegem.serialization.versioning.beans.singleversion.Dog;
import javassist.CannotCompileException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;

import java.io.*;
import java.util.Arrays;

/**
 * User: akondratyev
 * @author Andrey Stepanov aka standy
 */
public class XDeserializeForVersioningTest {
    @BeforeTest
    public void before() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(XDeserializeForVersioningTest.class.getClassLoader(),
                Dog.class, Company.class, IllegalVersion.class, Son.class, Car.class, Person.class);
    }

    @Test
    public void deserializeSingleVersion() throws IOException, CannotCompileException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("dog.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("dog.versionTest"));
        in.readFully(buf);
        in.close();

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);

        Dog dog = DataSerializer.readObject(new DataInputStream(byteArray));
        assertThat(dog.getName()).isEqualTo("Rex");
    }

    @Test
    public void deserializePreviousVersion() throws IOException, CannotCompileException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("simpleCompany.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("simpleCompany.versionTest"));
        in.readFully(buf);
        in.close();

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);

        Company c2 = DataSerializer.readObject(new DataInputStream(byteArray));
        assertThat(c2.getId()).isEqualTo(123);
        assertThat(c2.getName()).isNull();
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void deserializeNewVersionFromOldClass() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("restoreNewClassVersion.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("restoreNewClassVersion.versionTest"));
        in.readFully(buf);
        in.close();

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);

        DataSerializer.readObject(new DataInputStream(byteArray));
    }

    @Test
    public void deserializeWithInheritance() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("son.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("son.versionTest"));
        in.readFully(buf);
        in.close();

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);

        Son son= DataSerializer.readObject(new DataInputStream(byteArray));
        assertThat(son.getId()).isEqualTo(0);
        assertThat(son.getName()).as("son's name");
        assertThat(son.getAge()).isEqualTo(23);
        assertThat(son.getSisters()).isNull();
        assertThat(son.getBurthday()).isNull();
        assertThat(son.getBrothers()).isEqualTo(Arrays.asList(4L, 3L, 5L, 1L));
    }

    @Test
    public void deserializeCarVersionThree() throws IOException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("cars.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("cars.versionTest"));
        in.readFully(buf);
        in.close();

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);
        Car car = DataSerializer.readObject(new DataInputStream(byteArray));
        assertThat(car.getModel()).isEqualTo("golf");
        assertThat(car.getVersion()).isEqualTo("5");
        assertThat(car.getSeatCount()).isEqualTo(4);
        assertThat(car.isSedan()).isTrue();
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void deserializeWithNewClassModelAndOldBeanVersion() throws IOException, CannotCompileException, ClassNotFoundException {
        byte[] buf = new byte[(int) new File("person.versionTest").length()];
        DataInputStream in = new DataInputStream(new FileInputStream("person.versionTest"));
        in.readFully(buf);
        in.close();

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buf);
        DataSerializer.readObject(new DataInputStream(byteArray));
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
