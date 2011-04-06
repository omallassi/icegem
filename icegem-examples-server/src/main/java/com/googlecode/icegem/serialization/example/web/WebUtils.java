package com.googlecode.icegem.serialization.example.web;

import com.googlecode.icegem.serialization.example.bean.Address;
import com.googlecode.icegem.serialization.example.bean.Company;
import com.googlecode.icegem.serialization.example.bean.Person;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author igolovach
 */

public class WebUtils {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static Company produceNextCompany() {
        Company result = new Company();

        final int index = counter.getAndIncrement();

        // parent parent field
        result.setParentParentData(index * 321);
        // parent field
        result.setParentData(index * 123);
        // normal fields
        result.setId(index);
        result.setName("Name-" + index);
        result.setWorkerCount(15000 + index);
        result.setBoss(producePerson());
        result.setMainOfficeAddress(produceAddress());
        // not @Serialized field
        result.setTransientCache(new String[]{"aaa", "bbb", "ccc"});

        return result;
    }

    public static Person producePerson() {
        final Person result = new Person();

        final int index = counter.getAndIncrement();
        result.setAge(10 + index);
        result.setFirstName("Mike-" + index);
        result.setSecondName("Johnson-" + index);
        result.setHomeAddress(produceAddress());

        return result;
    }

    public static Address produceAddress() {
        final Address result = new Address();

        final int index = counter.getAndIncrement();

        result.setParentData(index * 777);
        result.setStreet("Street-" + index);
        result.setZipCode("ZipCode-" + index);

        return result;
    }
}
