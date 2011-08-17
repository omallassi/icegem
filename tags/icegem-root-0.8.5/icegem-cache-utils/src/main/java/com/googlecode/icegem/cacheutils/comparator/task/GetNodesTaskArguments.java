package com.googlecode.icegem.cacheutils.comparator.task;

import java.io.Serializable;
import java.util.List;

public class GetNodesTaskArguments implements Serializable {

	private static final long serialVersionUID = 8148867868557140116L;
	
	private String mode;
	private String address;
	private String regionName;
	private String filename;
	private int loadFactor;
	private List<String> packages;
	private long[] ids;
	private int shift;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getLoadFactor() {
		return loadFactor;
	}

	public void setLoadFactor(int loadFactor) {
		this.loadFactor = loadFactor;
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public long[] getIds() {
		return ids;
	}

	public void setIds(long[] ids) {
		this.ids = ids;
	}

	public int getShift() {
		return shift;
	}

	public void setShift(int shift) {
		this.shift = shift;
	}

}
