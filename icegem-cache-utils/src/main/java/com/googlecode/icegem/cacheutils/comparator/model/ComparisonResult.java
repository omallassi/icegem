package com.googlecode.icegem.cacheutils.comparator.model;

import java.util.HashSet;
import java.util.Set;

public class ComparisonResult {
	private Set<Object> extra = new HashSet<Object>();
	private Set<Object> missed = new HashSet<Object>();
	private Set<Object> different = new HashSet<Object>();

	public Set<Object> getExtra() {
		return extra;
	}

	public Set<Object> getMissed() {
		return missed;
	}

	public Set<Object> getDifferent() {
		return different;
	}

	public void addExtra(Object o) {
		extra.add(o);
	}

	public void addMissed(Object o) {
		missed.add(o);
	}

	public void addDifferent(Object o) {
		different.add(o);
	}

	public void addAllExtra(Set<Object> set) {
		extra.addAll(set);
	}

	public void addAllMissed(Set<Object> set) {
		missed.addAll(set);
	}

	public void addAllDifferent(Set<Object> set) {
		different.addAll(set);
	}

	public boolean isEmpty() {
		return extra.isEmpty() && missed.isEmpty() && different.isEmpty();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("extra = ").append(extra).append("\n");
		sb.append("missed = ").append(missed).append("\n");
		sb.append("different = ").append(different);

		return sb.toString();
	}
}
