package com.googlecode.icegem.serialization.primitive;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;

/**
 * Bean for test purposes.
 * Contains fields of all types.
 *
 * @author igolovach
 */

@AutoSerializable(dataSerializerID = 14)
@BeanVersion(1)
public class WrapperBean {
    private Boolean boolean_;
    private Byte byte_;
    private Character character_;
    private Short short_;
    private Integer integer_;
    private Long long_;
    private Float float_;
    private Double double_;

    public Boolean getBoolean_() {
        return boolean_;
    }

    public void setBoolean_(Boolean boolean_) {
        this.boolean_ = boolean_;
    }

    public Byte getByte_() {
        return byte_;
    }

    public void setByte_(Byte byte_) {
        this.byte_ = byte_;
    }

    public Character getCharacter_() {
        return character_;
    }

    public void setCharacter_(Character character_) {
        this.character_ = character_;
    }

    public Short getShort_() {
        return short_;
    }

    public void setShort_(Short short_) {
        this.short_ = short_;
    }

    public Integer getInteger_() {
        return integer_;
    }

    public void setInteger_(Integer integer_) {
        this.integer_ = integer_;
    }

    public Long getLong_() {
        return long_;
    }

    public void setLong_(Long long_) {
        this.long_ = long_;
    }

    public Float getFloat_() {
        return float_;
    }

    public void setFloat_(Float float_) {
        this.float_ = float_;
    }

    public Double getDouble_() {
        return double_;
    }

    public void setDouble_(Double double_) {
        this.double_ = double_;
    }
}
