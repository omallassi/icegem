package com.googlecode.icegem.serialization.codegen.impl;

import com.googlecode.icegem.serialization.codegen.XField;

/**
 * Convert XField to code-string that read data from bean field by getter and write it to DataOutput
 *
 * @author igolovach
 */

public interface ToDataProcessor {

    public String process(XField field);
}
