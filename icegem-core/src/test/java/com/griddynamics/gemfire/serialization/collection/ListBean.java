package com.griddynamics.gemfire.serialization.collection;

import com.griddynamics.gemfire.serialization.SerializedClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author igolovach
 */

@SerializedClass(dataSerializerID = 8)
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
