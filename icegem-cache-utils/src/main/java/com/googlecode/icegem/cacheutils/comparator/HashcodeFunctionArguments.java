package com.googlecode.icegem.cacheutils.comparator;

import java.io.Serializable;
import java.util.List;

public class HashcodeFunctionArguments implements Serializable {

	private static final long serialVersionUID = 2841156547651691453L;

	private int loadFactor;
	private List<String> packages;

	public HashcodeFunctionArguments(int loadFactor, List<String> packages) {
		this.loadFactor = loadFactor;
		this.packages = packages;
	}

	public int getLoadFactor() {
		return loadFactor;
	}

	public List<String> getPackages() {
		return packages;
	}
	
}
