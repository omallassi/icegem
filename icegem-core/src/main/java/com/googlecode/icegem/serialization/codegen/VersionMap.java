/**
 * 
 */
package com.googlecode.icegem.serialization.codegen;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This is optimized version of map to use during de/serialization for metadata validation.
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
public class VersionMap {
	
	private short[] hashCodes;
	
	private byte baseVersion;
	
	private String className;
	
	public VersionMap(String className, int baseVersion, int size) {
		this.className = className;
		this.baseVersion = (byte) baseVersion;
		this.hashCodes = new short[size];
	}

	public void put(byte version, short hash) {
		int offset = baseVersion - version;
		if(offset < 0 || offset >= hashCodes.length) {
			throw new IllegalArgumentException("Version " + version + " is not supposed to be stored");
		}
		hashCodes[offset] = hash;
	}
	
	public void validate(byte version, short hash) {
		int offset = baseVersion - version;
		if(offset >= 0 && offset < hashCodes.length) {
			if(hash != this.hashCodes[offset]) {
				String message = String.format("Metadata of class %s version %s different in serializer and binary form. Check all " +
						"properties have @SinceVersion notification and no property have been deleted or mutated",
						this.className, version);
				throw new ClassCastException(message);
			}
		}
	}
	
	public void writeAll(DataOutput out) throws IOException {
		for(int i = 0; i < hashCodes.length; i++) {
			out.writeShort(hashCodes[i]);
		}
	}
	
	public void readAndCheck(DataInput in, byte actualVersion, byte len) throws IOException {
		for(int i = 0 ; i < len; i++) {
			short hashFromBinary = in.readShort();
			validate((byte) (actualVersion - i), hashFromBinary);
		}
	}
}
