package com.griddynamics.icegem.serialization._inheritance.serializedclass.parenconcrete;

/**
 * @author igolovach
 */
public class NotMarkedChildOfMarkedParent extends ParentMarked {
    private int childData;

    public int getChildData() {
        return childData;
    }

    public void setChildData(int childData) {
        this.childData = childData;
    }
}
