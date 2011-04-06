package com.googlecode.icegem.serialization.primitive;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * Bean for test purposes.
 * Contains fields of all types.
 *
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 12)
@BeanVersion(1)
public class PrimitiveBean {
    private boolean bool;
    private byte byt;
    private char ch;
    private short sh;
    private int in;
    private long l;
    private float f;
    private double d;

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public byte getByt() {
        return byt;
    }

    public void setByt(byte byt) {
        this.byt = byt;
    }

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public short getSh() {
        return sh;
    }

    public void setSh(short sh) {
        this.sh = sh;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    /*    public boolean getBoolean() {
        return bool;
    }

    public void setBoolean(boolean _boolean) {
        this.bool = _boolean;
    }

    public byte getByte() {
        return byt;
    }

    public void setByte(byte _byte) {
        this.byt = _byte;
    }

    public char getChar() {
        return ch;
    }

    public void setChar(char _char) {
        this.ch = _char;
    }

    public short getShort() {
        return sh;
    }

    public void setShort(short _short) {
        this.sh = _short;
    }

    public int getInt() {
        return in;
    }

    public void setInt(int _int) {
        this.in = _int;
    }

    public long getLong() {
        return l;
    }

    public void setLong(long _long) {
        this.l = _long;
    }

    public float getFloat() {
        return f;
    }

    public void setFloat(float _float) {
        this.f = _float;
    }

    public double getDouble() {
        return d;
    }

    public void setDouble(double _double) {
        this.d = _double;
    }*/
}
