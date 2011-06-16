package com.googlecode.icegem.serialization.versioning;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import com.googlecode.icegem.serialization.versioning.beans.wrong.Bird;
import com.googlecode.icegem.serialization.versioning.beans.wrong.Cat;
import com.googlecode.icegem.serialization.versioning.beans.singleversion.Dog;
import com.googlecode.icegem.serialization.versioning.beans.previousversion.beanv1.Company;
import com.googlecode.icegem.serialization.versioning.beans.modified.beanv1.Person;
import com.googlecode.icegem.serialization.versioning.beans.incorrect.v1.IllegalVersion;
import com.googlecode.icegem.serialization.versioning.beans.inheritance.v1.Son;
import com.googlecode.icegem.serialization.versioning.beans.manyVersions.v1.Car;

import com.googlecode.icegem.serialization.versioning.beans.wrong.Fish;
import javassist.CannotCompileException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: akondratyev
 * @author Andrey Stepanov aka standy
 */
public class SerializeForVersioningTest extends TestParent {
    @BeforeTest
    public void register() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(SerializeForVersioningTest.class.getClassLoader(),
                Dog.class, Company.class, IllegalVersion.class, Son.class, Car.class, Person.class);
    }

    @Test
    public void serializeClassWithSingleVersion() throws IOException, CannotCompileException {
        Dog dog = new Dog("Rex");
        DataSerializer.writeObject(dog, new DataOutputStream(new FileOutputStream("dog.versionTest")));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void serializeClassWithNegativeVersion() throws IOException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), Cat.class);
        Cat cat = new Cat("Murka");
        DataSerializer.writeObject(cat, new DataOutputStream(new FileOutputStream("cat.versionTest")));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void serializeClassWithNegativeFiledVersion() throws IOException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), Bird.class);
        Bird bird = new Bird("Kesha");
        DataSerializer.writeObject(bird, new DataOutputStream(new FileOutputStream("bird.versionTest")));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void serializeClassWithoutBeanVersionAnnotation() throws IOException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), Fish.class);
        Fish bird = new Fish("Fish");
        DataSerializer.writeObject(bird, new DataOutputStream(new FileOutputStream("fish.versionTest")));
    }

    @Test
    public void serializeSimpleCompany() throws IOException, CannotCompileException {
        Company company = new Company();
        company.setId(123);
        DataSerializer.writeObject(company, new DataOutputStream(new FileOutputStream("simpleCompany.versionTest")));
    }

    @Test
    public void serializeNewClassVersion() throws CannotCompileException, IOException {
        IllegalVersion illegalVersion = new IllegalVersion();
        DataSerializer.writeObject(illegalVersion, new DataOutputStream(new FileOutputStream("restoreNewClassVersion.versionTest")));
    }

    @Test
    public void serializeWithInheritance() throws CannotCompileException, IOException {
        Son son = new Son();
        son.setName("son's name");
        son.setAge(23);
        son.setBrothers(new ArrayList<Long>(Arrays.asList(4L, 3L, 5L, 1L)));
        DataSerializer.writeObject(son, new DataOutputStream(new FileOutputStream("son.versionTest")));
    }
    
    @Test
    public void serializeCarVersionOne() throws IOException {
        Car car = new Car("golf", "5");
        DataSerializer.writeObject(car, new DataOutputStream(new FileOutputStream("cars.versionTest")));
    }

    @Test
    public void serializePersonVersionOne() throws IOException {
        Person person = new Person(123);
        DataSerializer.writeObject(person, new DataOutputStream(new FileOutputStream("person.versionTest")));
    }
}
