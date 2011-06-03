package com.googlecode.icegem.cacheutils.replication.relations;

/**
 * Represents a relation between two guest nodes (or two clusters)
 */
public class Relation {

	private String from;
	private String to;
	private long duration = -1;

	public Relation(String from, String to) {
		this.from = from;
		this.to = to;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}
	
	public RelationState getState() {
		return (duration == -1 ? RelationState.NEW : RelationState.CONNECTED);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		Relation other = (Relation) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

}
