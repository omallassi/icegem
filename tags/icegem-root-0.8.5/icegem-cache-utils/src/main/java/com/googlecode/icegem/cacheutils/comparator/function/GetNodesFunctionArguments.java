package com.googlecode.icegem.cacheutils.comparator.function;

import java.io.Serializable;
import java.util.List;

public class GetNodesFunctionArguments implements Serializable {

	private static final long serialVersionUID = 2841156547651691453L;

	private int loadFactor;
	private List<String> packages;
	private final long[] ids;
	private int shift;

	public GetNodesFunctionArguments(int loadFactor, List<String> packages,
		long[] ids, int shift) {

		this.loadFactor = loadFactor;
		this.packages = packages;
		this.ids = ids;
		this.shift = shift;

	}

	public int getLoadFactor() {
		return loadFactor;
	}

	public List<String> getPackages() {
		return packages;
	}

	public long[] getIds() {
		return ids;
	}

	public int getShift() {
		return shift;
	}

}
