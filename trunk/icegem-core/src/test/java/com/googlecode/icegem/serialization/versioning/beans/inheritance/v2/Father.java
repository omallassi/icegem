package com.googlecode.icegem.serialization.versioning.beans.inheritance.v2;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: akondratyev
 */
@AutoSerializable(dataSerializerID = 8763)
@BeanVersion(2)
public class Father {
    private int id;
    private String name;
    private int age;
    private Date birthday;
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

    @SinceVersion(2)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @SinceVersion(2)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SinceVersion(3)
    public List<Son> getChildren() {
        return children;
    }

    public void setChildren(List<Son> children) {
        this.children = children;
    }
}
