package com.googlecode.icegem.cacheutils.monitor.controller.event;

import com.googlecode.icegem.cacheutils.common.Utils;
import com.googlecode.icegem.cacheutils.monitor.controller.model.Node;

/**
 * Represents the node event.
 */
public class NodeEvent {

	private long createdAt;
	private Node node;
	private NodeEventType type;

	public NodeEvent(Node node, NodeEventType type) {
		if ((node == null) || (type == null)) {
			throw new IllegalArgumentException("The node and type cannot be null"); 
		}
		
		this.node = node;
		this.type = type;
		createdAt = System.currentTimeMillis();
	}

	public Node getNode() {
		return node;
	}

	public NodeEventType getType() {
		return type;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(Utils.dateToString(createdAt)).append("  ").append(type).append("  ")
			.append(node);

		return sb.toString();
	}
}
