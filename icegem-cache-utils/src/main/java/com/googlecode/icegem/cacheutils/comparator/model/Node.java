package com.googlecode.icegem.cacheutils.comparator.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Node implements Serializable {

	private static final long serialVersionUID = 6882283061702312942L;

	private long id;
	private long hashcode = 0;
	private Object data;

	private Node[] children;

	public Node(long id) {
		this.id = id;
		this.children = new Node[0];
	}

	public void merge(Node other) {
		if (other.getId() != id) {
			throw new IllegalArgumentException(
				"Id of merged node must be the same as id of this node");
		}

		addHashcode(other.getHashcode());

		Node[] otherChildren = other.getChildren();

		for (Node otherChild : otherChildren) {
			Node child = getChild(otherChild.getId());
			if (child == null) {
				addChild(otherChild);
			} else {
				child.merge(otherChild);
			}
		}
	}

	public long getId() {
		return id;
	}

	public long getHashcode() {
		return hashcode;
	}

	public Node[] getChildren() {
		return children;
	}

	public Set<Long> getChildrenIdsSet() {
		Set<Long> result = new HashSet<Long>();

		for (Node node : children) {
			result.add(node.getId());
		}

		return result;
	}

	public Set<Object> getChildrenDataSet() {
		Set<Object> result = new HashSet<Object>();

		for (Node node : children) {
			result.add(node.getData());
		}

		return result;
	}

	public void addChild(Node child) {
		if (!containsChild(child)) {
			int length = children.length;
			Node[] newChildren = new Node[length + 1];
			System.arraycopy(children, 0, newChildren, 0, length);
			children = newChildren;
			children[length] = child;
		}
	}

	public Node getChild(long childId) {
		Node result = null;

		for (Node child : children) {
			if (child.getId() == childId) {
				result = child;
				break;
			}
		}

		return result;
	}

	private boolean containsChild(Node child) {
		for (Node node : children) {
			if (node.equals(child)) {
				return true;
			}
		}
		return false;
	}

	public void addHashcode(long hashcode) {
		this.hashcode += hashcode;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "[" + Long.toHexString(id) + ", " + Long.toHexString(hashcode)
			+ ", " + data + ", "
			+ (children == null ? "null" : Arrays.asList(children)) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
