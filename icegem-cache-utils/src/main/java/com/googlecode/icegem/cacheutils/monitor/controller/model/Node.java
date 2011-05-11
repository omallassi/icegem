package com.googlecode.icegem.cacheutils.monitor.controller.model;

import com.gemstone.gemfire.cache.client.Pool;
import com.googlecode.icegem.cacheutils.monitor.utils.Utils;

/**
 * Represents the node - the set of information related to the instance of
 * GemFire cache server.
 */
public class Node {
	private String host;
	private int port;
	private Pool pool;
	private NodeStatus status;
	private long statusChangedAt = -1;

	public Node(String host, int port, Pool pool) {
		this.host = host;
		this.port = port;
		this.pool = pool;
		setStatus(NodeStatus.NEW);
	}

	public void markAsAlive() {
		setStatus(NodeStatus.ALIVE);
	}

	public void markAsDead() {
		setStatus(NodeStatus.DEAD);
	}

	private void setStatus(NodeStatus status) {
		if (!status.equals(this.status)) {
			this.status = status;
			statusChangedAt = System.currentTimeMillis();
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Pool getPool() {
		return pool;
	}

	public NodeStatus getStatus() {
		return status;
	}

	public long getStatusChangedAt() {
		return statusChangedAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
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
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[");
		sb.append(host).append(":").append(port).append(", ");
		// sb.append(pool).append(", ");
		sb.append(status).append(", ");
		sb.append(Utils.dateToString(statusChangedAt));
		sb.append("]");

		return sb.toString();
	}

}
