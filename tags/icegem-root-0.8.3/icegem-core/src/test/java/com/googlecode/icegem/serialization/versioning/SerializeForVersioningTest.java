package com.googlecode.icegem.serialization.versioning;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;
import com.googlecode.icegem.serialization.versioning.beans.versionhistory.v1.Mouse;
import com.googlecode.icegem.serialization.versioning.beans.wrong.*;
import com.googlecode.icegem.serialization.versioning.beans.singleversion.Dog;
import com.googlecode.icegem.serialization.versioning.beans.previousversion.beanv1.Company;
import com.googlecode.icegem.serialization.versioning.beans.modified.beanv1.Person;
import com.googlecode.icegem.serialization.versioning.beans.inheritance.v1.Son;
import com.googlecode.icegem.serialization.versioning.beans.manyVersions.v1.Car;

import com.googlecode.icegem.serialization.versioning.beans.wrong.v1.Man;
import com.googlecode.icegem.serialization.versioning.beans.wrong.v1.Rabbit;
import com.googlecode.icegem.serialization.versioning.beans.wrong.v1.Table;
import com.googlecode.icegem.serialization.versioning.beans.wrong.v1.Woman;
import com.googlecode.icegem.serialization.versioning.beans.wrong.v2.Bear;
import com.googlecode.icegem.serialization.versioning.beans.versionhistory.v1.Keyboard;
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
        HierarchyRegistry.registerAll(getContextClassLoader(),
                Dog.class, Bear.class, Company.class, Son.class,
                Car.class, Person.class, Rabbit.class, Man.class,
                Woman.class, Table.class, Keyboard.class, Mouse.class);
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
    public void serializeClassWithNewVersion() throws IOException, CannotCompileException {
        Bear bear = new Bear();
        DataSerializer.writeObject(bear, new DataOutputStream(new FileOutputStream("bear.versionTest")));
    }

    @Test
    public void serializeSimpleCompany() throws IOException, CannotCompileException {
        Company company = new Company();
        company.setId(123);
        DataSerializer.writeObject(company, new DataOutputStream(new FileOutputStream("simpleCompany.versionTest")));
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

    @Test
    public void serializeClassVerisonOne() throws IOException, CannotCompileException {
        DataSerializer.writeObject(new Rabbit(), new DataOutputStream(new FileOutputStream("rabbit.versionTest")));
    }

    @Test
    public void serializeManClassVerisonOne() throws IOException, CannotCompileException {
        DataSerializer.writeObject(new Man(), new DataOutputStream(new FileOutputStream("man.versionTest")));
    }

    @Test
    public void serializeWomanClassVerisonOne() throws IOException, CannotCompileException {
        DataSerializer.writeObject(new Woman(), new DataOutputStream(new FileOutputStream("woman.versionTest")));
    }

    @Test
    public void serializeTableClassVerisonOne() throws IOException, CannotCompileException {
        DataSerializer.writeObject(new Table(), new DataOutputStream(new FileOutputStream("table.versionTest")));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void serializeClassWithNegativeHeaderVersion() throws IOException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), Chair.class);
        DataSerializer.writeObject(new Chair(), new DataOutputStream(new FileOutputStream("chair.versionTest")));
    }

    @Test(expectedExceptions = InvalidClassException.class)
    public void serializeClassWithoutAutoSerializableAnnotation() throws IOException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), Pig.class);
        DataSerializer.writeObject(new Pig(), new DataOutputStream(new FileOutputStream("pig.versionTest")));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void serializeClassWithNegativeVersionHistoryLength() throws IOException, CannotCompileException {
        HierarchyRegistry.registerAll(getContextClassLoader(), Computer.class);
        DataSerializer.writeObject(new Computer(), new DataOutputStream(new FileOutputStream("computer.versionTest")));
    }

    @Test
    public void serializeKeyboardClassVersionOne() throws IOException, CannotCompileException {
        DataSerializer.writeObject(new Keyboard(), new DataOutputStream(new FileOutputStream("keyboard.versionTest")));
    }

    @Test
    public void serializeMouseClassVersionOne() throws IOException, CannotCompileException {
        DataSerializer.writeObject(new Mouse(), new DataOutputStream(new FileOutputStream("mouse.versionTest")));
    }
}
