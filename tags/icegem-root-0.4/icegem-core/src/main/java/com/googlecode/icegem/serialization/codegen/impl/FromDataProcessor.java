package com.googlecode.icegem.serialization.codegen.impl;

import com.googlecode.icegem.serialization.codegen.XField;

/**
 * Convert XField to code-string that read data from DataInput and write it to bean field by setter
 *
 * @author igolovach
 */

public interface FromDataProcessor {

    public String process(XField field);
}
