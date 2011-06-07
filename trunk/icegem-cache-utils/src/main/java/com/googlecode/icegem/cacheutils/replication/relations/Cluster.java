package com.googlecode.icegem.cacheutils.replication.relations;

public class Cluster {

	private String name;
	private String locators;

	public Cluster(String name, String locators) {
		this.name = name;
		this.locators = locators;
	}

	public String getName() {
		return name;
	}

	public String getLocators() {
		return locators;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
