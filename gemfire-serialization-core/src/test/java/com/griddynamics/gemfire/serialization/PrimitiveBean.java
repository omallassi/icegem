package com.griddynamics.gemfire.serialization;

/**
 * Bean for test purposes.
 * Contains fields of all types.
 *
 * @author igolovach
 */

@SerializedClass(dataSerializerID = 12)
public class PrimitiveBean {
    private boolean _boolean;
    private byte _byte;
    private char _char;
    private short _short;
    private int _int;
    private long _long;
    private float _float;
    private double _double;

    public boolean getBoolean() {
        return _boolean;
    }

    public void setBoolean(boolean _boolean) {
        this._boolean = _boolean;
    }

    public byte getByte() {
        return _byte;
    }

    public void setByte(byte _byte) {
        this._byte = _byte;
    }

    public char getChar() {
        return _char;
    }

    public void setChar(char _char) {
        this._char = _char;
    }

    public short getShort() {
        return _short;
    }

    public void setShort(short _short) {
        this._short = _short;
    }

    public int getInt() {
        return _int;
    }

    public void setInt(int _int) {
        this._int = _int;
    }

    public long getLong() {
        return _long;
    }

    public void setLong(long _long) {
        this._long = _long;
    }

    public float getFloat() {
        return _float;
    }

    public void setFloat(float _float) {
        this._float = _float;
    }

    public double getDouble() {
        return _double;
    }

    public void setDouble(double _double) {
        this._double = _double;
    }
}
