/**
 * 
 */
package com.googlecode.icegem.serialization.versioning.beans.versionhistory;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.SinceVersion;

/**
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
@AutoSerializable(dataSerializerID=123, versionHistoryLength=2)
@BeanVersion(4)
public class OldBean {
	
	private String s1;
	
	private String s2;
	
	private String s3;
	
	public OldBean() {
		super();
	}
	
	@SinceVersion(1)
	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	@SinceVersion(2)
	public String getS2() {
		return s2;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}

	@SinceVersion(3)
	public String getS3() {
		return s3;
	}

	public void setS3(String s3) {
		this.s3 = s3;
	}

	@SinceVersion(4)
	public String getS4() {
		return s4;
	}

	public void setS4(String s4) {
		this.s4 = s4;
	}

	private String s4;

}
