package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

@AutoSerializable(dataSerializerID = 1)
@BeanVersion(3)
public class SortOrderStub2 extends SortOrderStub1 {
	private String field4;
	private String field5;
	
	@SinceVersion(1)
	public String getField4() {
		return field4;
	}
	public void setField4(String field1) {
		this.field4 = field1;
	}
	
	@SinceVersion(2)
	public String getField5() {
		return field5;
	}
	public void setField5(String field2) {
		this.field5 = field2;
	}
}