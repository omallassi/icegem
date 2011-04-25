package com.googlecode.icegem.serialization.versioning;

import com.gemstone.gemfire.DataSerializer;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.versioning.beans.beanv1.Company;
import com.googlecode.icegem.serialization.versioning.incorrect.v1.IllegalVersion;
import com.googlecode.icegem.serialization.versioning.inheritance.v1.Son;
import com.googlecode.icegem.serialization.versioning.manyVersions.v1.Car;

import javassist.CannotCompileException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: akondratyev
 */
public class SerializeForVersioning {

    @BeforeTest
    public void register() throws InvalidClassException, CannotCompileException {
        HierarchyRegistry.registerAll(SerializeForVersioning.class.getClassLoader(),
                Company.class, IllegalVersion.class, Son.class, Car.class);
    }


    @Test(groups = "serialize", enabled = true)
    public void serializeSimpleCompany() throws IOException, CannotCompileException {
        Company company = new Company();
        company.setId(123);
        DataSerializer.writeObject(company, new DataOutputStream(new FileOutputStream("simpleCompany.versionTest")));
    }

    @Test(groups = "serialize", enabled = true)
    public void serializeNewClassVersion() throws CannotCompileException, IOException {
        IllegalVersion illegalVersion = new IllegalVersion();
        DataSerializer.writeObject(illegalVersion, new DataOutputStream(new FileOutputStream("restoreNewClassVersion.versionTest")));
    }

    @Test(groups = "serialize", enabled = true)
    public void serializeWithInheritance() throws CannotCompileException, IOException {
        Son son = new Son();
        son.setName("son's name");
        son.setAge(23);
        son.setBrothers(new ArrayList<Long>(Arrays.asList(4L, 3L, 5L, 1L)));
        DataSerializer.writeObject(son, new DataOutputStream(new FileOutputStream("restoreNewClassVersion.versionTest")));
    }
    
    @Test
    public void serializeCarVersionOne() throws IOException {
        Car car = new Car("golf", "5");
        DataSerializer.writeObject(car, new DataOutputStream(new FileOutputStream("cars.versionTest")));
    }

}
