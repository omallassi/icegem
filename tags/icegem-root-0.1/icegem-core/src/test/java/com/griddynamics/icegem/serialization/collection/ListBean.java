package com.griddynamics.icegem.serialization.collection;

import com.griddynamics.icegem.serialization.AutoSerializable;
import com.griddynamics.icegem.serialization.BeanVersion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 8)
@BeanVersion(1)
public class ListBean {
    private ArrayList arrayList;
    private LinkedList linkedList;
    private List list;

    public ArrayList getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList arrayList) {
        this.arrayList = arrayList;
    }

    public LinkedList getLinkedList() {
        return linkedList;
    }

    public void setLinkedList(LinkedList linkedList) {
        this.linkedList = linkedList;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}