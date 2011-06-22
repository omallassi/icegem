package com.googlecode.icegem.serialization.versioning.beans.inheritance.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.FieldVersion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Date birthday;
    @FieldVersion(since = 2)
    private List<Son> children;

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Son> getChildren() {
        return children;
    }

    public void setChildren(List<Son> children) {
        this.children = children;
    }
}
