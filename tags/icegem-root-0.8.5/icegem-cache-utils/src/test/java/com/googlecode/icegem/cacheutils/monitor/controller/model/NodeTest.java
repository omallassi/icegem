package com.googlecode.icegem.cacheutils.monitor.controller.model;

import org.junit.Test;

import com.gemstone.gemfire.cache.client.Pool;
import com.googlecode.icegem.cacheutils.common.Utils;
import static org.junit.Assert.*;


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

		assertEquals(node.getStatus(), NodeStatus.ALIVE);
	}

	@Test
	public void testMarkAsDead() {
		Node node = createNode();

		node.markAsDead();

		assertEquals(node.getStatus(), NodeStatus.DEAD);
	}

	@Test
	public void testGetHost() {
		Node node = createNode();

		assertEquals(node.getHost(), HOST);
	}

	@Test
	public void testGetPort() {
		Node node = createNode();

		assertEquals(node.getPort(), PORT);
	}

	@Test
	public void testGetPool() {
		Node node = createNode();

		assertNull(node.getPool());
	}

	@Test
	public void testGetStatus() {
		Node node = createNode();
		assertEquals(node.getStatus(), NodeStatus.NEW);

		node.markAsAlive();
		assertEquals(node.getStatus(), NodeStatus.ALIVE);

		node.markAsDead();
		assertEquals(node.getStatus(), NodeStatus.DEAD);
	}

	@Test
	public void testGetStatusChangedAt() {
		Node node = createNode();

		node.markAsAlive();
		long firstStatusChangedAt = node.getStatusChangedAt();

		node.markAsAlive();
		long secondStatusChangedAt = node.getStatusChangedAt();

		assertTrue(firstStatusChangedAt>-1);
		assertTrue(secondStatusChangedAt>-1);
		assertEquals(secondStatusChangedAt, firstStatusChangedAt);

		node.markAsDead();
		firstStatusChangedAt = node.getStatusChangedAt();

		node.markAsDead();
		secondStatusChangedAt = node.getStatusChangedAt();

		assertTrue(firstStatusChangedAt>-1);
		assertTrue(secondStatusChangedAt>-1);
		assertEquals(secondStatusChangedAt, firstStatusChangedAt);
	}

	@Test
	public void testToString() {
		Node node = createNode();

		assertEquals(node.toString(), 
			"[" + node.getHost() + ":" + node.getPort() + ", "
				+ node.getStatus() + ", "
				+ Utils.dateToString(node.getStatusChangedAt()) + "]");
	}
}
