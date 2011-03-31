package com.griddynamics.icegem.serialization.example.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;
import com.griddynamics.icegem.serialization.Transient;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 2)
@BeanVersion(1)
public class Company extends ParentCompany {
    private Integer id;
    private String name;
    private int workerCount;
    private Address mainOfficeAddress;
    private Person boss;
    /**
     * Special field with 'null' value
     */
    private String nullStringField;
    /**
     * Special don't serialized field
     */
    private String[] transientCache;

    public Company() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public Person getBoss() {
        return boss;
    }

    public void setBoss(Person boss) {
        this.boss = boss;
    }

    public Address getMainOfficeAddress() {
        return mainOfficeAddress;
    }

    public void setMainOfficeAddress(Address mainOfficeAddress) {
        this.mainOfficeAddress = mainOfficeAddress;
    }

    @Transient
    public String[] getTransientCache() {
        return transientCache;
    }

    public void setTransientCache(String[] transientCache) {
        this.transientCache = transientCache;
    }

    public String getNullStringField() {
        return nullStringField;
    }

    public void setNullStringField(String nullStringField) {
        this.nullStringField = nullStringField;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
    }
}

