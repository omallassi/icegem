package com.griddynamics.gemfire.serialization;

/**
 * Bean for test purposes.
 * Contains fields of all types.
 *
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 14)
public class WrapperBean {
    private Boolean Boolean_;
    private Byte Byte_;
    private Character Character_;
    private Short Short_;
    private Integer Integer_;
    private Long Long_;
    private Float Float_;
    private Double Double_;

    public Boolean getBoolean_() {
        return Boolean_;
    }

    public void setBoolean_(Boolean boolean_) {
        Boolean_ = boolean_;
    }

    public Byte getByte_() {
        return Byte_;
    }

    public void setByte_(Byte byte_) {
        Byte_ = byte_;
    }

    public Character getCharacter_() {
        return Character_;
    }

    public void setCharacter_(Character character_) {
        Character_ = character_;
    }

    public Short getShort_() {
        return Short_;
    }

    public void setShort_(Short short_) {
        Short_ = short_;
    }

    public Integer getInteger_() {
        return Integer_;
    }

    public void setInteger_(Integer integer_) {
        Integer_ = integer_;
    }

    public Long getLong_() {
        return Long_;
    }

    public void setLong_(Long long_) {
        Long_ = long_;
    }

    public Float getFloat_() {
        return Float_;
    }

    public void setFloat_(Float float_) {
        Float_ = float_;
    }

    public Double getDouble_() {
        return Double_;
    }

    public void setDouble_(Double double_) {
        Double_ = double_;
    }
}
