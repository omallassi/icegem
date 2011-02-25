package com.griddynamics.gemfire.serialization._enum;

import com.griddynamics.gemfire.serialization.AutoSerializable;

import java.util.List;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 102)
public class FieldEnumBean {
    private SimpleEnumBean simpleEnumBean;
    private ExtendedFinalEnumBean extendedFinalEnumBean;
    private ExtendedMutableEnumBean extendedMutableEnumBean;
    private Enum enumField;
    private Enum[] enumArray;
    private Object objectField;
    private Object[] objectArray;
    private SimpleEnumBean[] simpleEnumBeanArray;
    private List list;

    public SimpleEnumBean getSimpleEnumBean() {
        return simpleEnumBean;
    }

    public void setSimpleEnumBean(SimpleEnumBean simpleEnumBean) {
        this.simpleEnumBean = simpleEnumBean;
    }

    public ExtendedFinalEnumBean getExtendedFinalEnumBean() {
        return extendedFinalEnumBean;
    }

    public void setExtendedFinalEnumBean(ExtendedFinalEnumBean extendedFinalEnumBean) {
        this.extendedFinalEnumBean = extendedFinalEnumBean;
    }

    public ExtendedMutableEnumBean getExtendedMutableEnumBean() {
        return extendedMutableEnumBean;
    }

    public void setExtendedMutableEnumBean(ExtendedMutableEnumBean extendedMutableEnumBean) {
        this.extendedMutableEnumBean = extendedMutableEnumBean;
    }

    public Enum getEnumField() {
        return enumField;
    }

    public void setEnumField(Enum enumField) {
        this.enumField = enumField;
    }

    public Enum[] getEnumArray() {
        return enumArray;
    }

    public void setEnumArray(Enum[] enumArray) {
        this.enumArray = enumArray;
    }

    public Object getObjectField() {
        return objectField;
    }

    public void setObjectField(Object objectField) {
        this.objectField = objectField;
    }

    public Object[] getObjectArray() {
        return objectArray;
    }

    public void setObjectArray(Object[] objectArray) {
        this.objectArray = objectArray;
    }

    public SimpleEnumBean[] getSimpleEnumBeanArray() {
        return simpleEnumBeanArray;
    }

    public void setSimpleEnumBeanArray(SimpleEnumBean[] simpleEnumBeanArray) {
        this.simpleEnumBeanArray = simpleEnumBeanArray;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
