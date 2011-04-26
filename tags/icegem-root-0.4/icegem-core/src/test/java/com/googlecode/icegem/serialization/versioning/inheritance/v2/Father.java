package com.googlecode.icegem.serialization.versioning.inheritance.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.FieldVersion;

import java.util.ArrayList;
import java.util.Date;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 8763)
@BeanVersion(2)
public class Father {
    @FieldVersion(since = 2)
    private int id;
    private String name;
    private int age;
    @FieldVersion(since = 2)
    private Date burthday;
    @FieldVersion(since = 2)
    private ArrayList<Son> children;

    public Father() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBurthday() {
        return burthday;
    }

    public void setBurthday(Date burthday) {
        this.burthday = burthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Son> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Son> children) {
        this.children = children;
    }
}
