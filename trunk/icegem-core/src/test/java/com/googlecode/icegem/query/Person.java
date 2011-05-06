package com.googlecode.icegem.query;

import java.io.Serializable;
import java.util.List;

/**
 * Domain class for tests.
 *
 * @author Andrey Stepanov aka standy
 */
public class Person implements Serializable {
    /** Field serialVersionUID  */
    private static final long serialVersionUID = -930346814776120969L;
    /** Field socialNumber  */
    private int socialNumber;
    /** Field children  */
    private List<String> children;

    /**
     * Constructor Person creates a new Person instance.
     *
     * @param socialNumber of type String
     * @param children of type List<String>
     */
    public Person(int socialNumber, List<String> children) {
        this.socialNumber = socialNumber;
        this.children = children;
    }

    /**
     * Method getSocialNumber returns the socialNumber of this Person object.
     *
     * @return the socialNumber (type String) of this Person object.
     */
    public int getSocialNumber() {
        return socialNumber;
    }

    /**
     * Method setSocialNumber sets the socialNumber of this Person object.
     *
     * @param socialNumber the socialNumber of this Person object.
     *
     */
    public void setSocialNumber(int socialNumber) {
        this.socialNumber = socialNumber;
    }

    /**
     * Method getChildren returns the children of this Person object.
     *
     * @return the children (type List<String>) of this Person object.
     */
    public List<String> getChildren() {
        return children;
    }

    /**
     * Method setChildren sets the children of this Person object.
     *
     * @param children the children of this Person object.
     *
     */
    public void setChildren(List<String> children) {
        this.children = children;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return socialNumber + " : " + children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (socialNumber != person.socialNumber) return false;
        if (children != null ? !children.equals(person.children) : person.children != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = socialNumber;
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}

