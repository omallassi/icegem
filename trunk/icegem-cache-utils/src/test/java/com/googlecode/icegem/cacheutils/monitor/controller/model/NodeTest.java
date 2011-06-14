package com.googlecode.icegem.cacheutils.monitor.controller.model;

import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

import com.gemstone.gemfire.cache.client.Pool;
import com.googlecode.icegem.cacheutils.common.Utils;

public class NodeTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 40404;
	private static final Pool POOL = null;

	private Node createNode() {
		return new Node(HOST, PORT, POOL);
	}

	@Test
	public void testMarkAsAlive() {
		Node node = createNode();

		node.markAsAlive();

		assertThat(node.getStatus()).isEqualTo(NodeStatus.ALIVE);
	}

	@Test
	public void testMarkAsDead() {
		Node node = createNode();

		node.markAsDead();

		assertThat(node.getStatus()).isEqualTo(NodeStatus.DEAD);
	}

	@Test
	public void testGetHost() {
		Node node = createNode();

		assertThat(node.getHost()).isEqualTo(HOST);
	}

	@Test
	public void testGetPort() {
		Node node = createNode();

		assertThat(node.getPort()).isEqualTo(PORT);
	}

	@Test
	public void testGetPool() {
		Node node = createNode();

		assertThat(node.getPool()).isNull();
	}

	@Test
	public void testGetStatus() {
		Node node = createNode();
		assertThat(node.getStatus()).isEqualTo(NodeStatus.NEW);

		node.markAsAlive();
		assertThat(node.getStatus()).isEqualTo(NodeStatus.ALIVE);

		node.markAsDead();
		assertThat(node.getStatus()).isEqualTo(NodeStatus.DEAD);
	}

	@Test
	public void testGetStatusChangedAt() {
		Node node = createNode();

		node.markAsAlive();
		long firstStatusChangedAt = node.getStatusChangedAt();

		node.markAsAlive();
		long secondStatusChangedAt = node.getStatusChangedAt();

		assertThat(firstStatusChangedAt).isGreaterThan(-1);
		assertThat(secondStatusChangedAt).isGreaterThan(-1);
		assertThat(secondStatusChangedAt).isEqualTo(firstStatusChangedAt);

		node.markAsDead();
		firstStatusChangedAt = node.getStatusChangedAt();

		node.markAsDead();
		secondStatusChangedAt = node.getStatusChangedAt();

		assertThat(firstStatusChangedAt).isGreaterThan(-1);
		assertThat(secondStatusChangedAt).isGreaterThan(-1);
		assertThat(secondStatusChangedAt).isEqualTo(firstStatusChangedAt);
	}

	@Test
	public void testToString() {
		Node node = createNode();

		assertThat(node.toString()).isEqualTo(
			"[" + node.getHost() + ":" + node.getPort() + ", "
				+ node.getStatus() + ", "
				+ Utils.dateToString(node.getStatusChangedAt()) + "]");
	}
}
