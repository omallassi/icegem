package com.griddynamics.icegem.serialization.perf.impl;

import com.griddynamics.icegem.serialization.AutoSerializable;

/**
 * @author igolovach
 */
@AutoSerializable(dataSerializerID = 2)
public class FlatFatPerfBean implements java.io.Serializable {
    private int ip0;
    private int ip1;
    private int ip2;

    private long lp0;
    private long lp1;
    private long lp2;

    private double d0;
    private double d1;
    private double d2;

    private Integer iw0;
    private Integer iw1;
    private Integer iw2;

    private Long lw0;
    private Long lw1;
    private Long lw2;

    private String string0;
    private String string1;
    private String string2;

    private int[] intArr0;
    private int[] intArr1;
    private int[] intArr2;

    public int getIp0() {
        return ip0;
    }

    public void setIp0(int ip0) {
        this.ip0 = ip0;
    }

    public int getIp1() {
        return ip1;
    }

    public void setIp1(int ip1) {
        this.ip1 = ip1;
    }

    public int getIp2() {
        return ip2;
    }

    public void setIp2(int ip2) {
        this.ip2 = ip2;
    }

    public long getLp0() {
        return lp0;
    }

    public void setLp0(long lp0) {
        this.lp0 = lp0;
    }

    public long getLp1() {
        return lp1;
    }

    public void setLp1(long lp1) {
        this.lp1 = lp1;
    }

    public long getLp2() {
        return lp2;
    }

    public void setLp2(long lp2) {
        this.lp2 = lp2;
    }

    public double getD0() {
        return d0;
    }

    public void setD0(double d0) {
        this.d0 = d0;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public double getD2() {
        return d2;
    }

    public void setD2(double d2) {
        this.d2 = d2;
    }

    public Integer getIw0() {
        return iw0;
    }

    public void setIw0(Integer iw0) {
        this.iw0 = iw0;
    }

    public Integer getIw1() {
        return iw1;
    }

    public void setIw1(Integer iw1) {
        this.iw1 = iw1;
    }

    public Integer getIw2() {
        return iw2;
    }

    public void setIw2(Integer iw2) {
        this.iw2 = iw2;
    }

    public Long getLw0() {
        return lw0;
    }

    public void setLw0(Long lw0) {
        this.lw0 = lw0;
    }

    public Long getLw1() {
        return lw1;
    }

    public void setLw1(Long lw1) {
        this.lw1 = lw1;
    }

    public Long getLw2() {
        return lw2;
    }

    public void setLw2(Long lw2) {
        this.lw2 = lw2;
    }

    public String getString0() {
        return string0;
    }

    public void setString0(String string0) {
        this.string0 = string0;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }

    public int[] getIntArr0() {
        return intArr0;
    }

    public void setIntArr0(int[] intArr0) {
        this.intArr0 = intArr0;
    }

    public int[] getIntArr1() {
        return intArr1;
    }

    public void setIntArr1(int[] intArr1) {
        this.intArr1 = intArr1;
    }

    public int[] getIntArr2() {
        return intArr2;
    }

    public void setIntArr2(int[] intArr2) {
        this.intArr2 = intArr2;
    }
}
