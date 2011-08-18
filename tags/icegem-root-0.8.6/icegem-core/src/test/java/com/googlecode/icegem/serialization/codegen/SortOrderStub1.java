package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.SinceVersion;

public class SortOrderStub1 {
	private String field1;
	private String field2;
	private String field3;
	
	@SinceVersion(1)
	public String getField1() {
		return field1;
	}
	
	@SinceVersion(3)
	public String getField3() {
		return field3;
	}
	
	public void setField3(String field3) {
		this.field3 = field3;
	}
	
	public void setField1(String field1) {
		this.field1 = field1;
	}
	
	@SinceVersion(2)
	public String getField2() {
		return field2;
	}
	public void setField2(String field2) {
		this.field2 = field2;
	}
}