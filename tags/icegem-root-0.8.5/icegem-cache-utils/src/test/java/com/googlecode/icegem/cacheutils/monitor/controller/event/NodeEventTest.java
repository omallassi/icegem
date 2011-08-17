package com.googlecode.icegem.cacheutils.monitor.controller.event;

import org.junit.Test;

import com.googlecode.icegem.cacheutils.common.Utils;
import com.googlecode.icegem.cacheutils.monitor.controller.model.Node;
import static org.junit.Assert.*;

public class NodeEventTest {

	private Node createNode() {
		return new Node("localhost", 40404, null);
	}

	private NodeEvent createNodeEvent(Node node) {
		return new NodeEvent(node, NodeEventType.ADDED);
	}

	@Test
	public void testGetNode() {
		Node node = createNode();
		NodeEvent event = createNodeEvent(node);

		assertEquals(event.getNode(), node);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNodeNullNode() {
		new NodeEvent(null, NodeEventType.ADDED);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNodeNullEventType() {
		Node node = createNode();
		new NodeEvent(node, null);
	}

	@Test
	public void testGetType() {
		Node node = createNode();
		NodeEvent event = createNodeEvent(node);

		assertEquals(event.getType(), NodeEventType.ADDED);
	}

	@Test
	public void testGetCreatedAt() {
		long testStartTime = System.currentTimeMillis();

		Node node = createNode();
		NodeEvent event = createNodeEvent(node);

		assertTrue(event.getCreatedAt()>=testStartTime);
	}

	@Test
	public void testToString() {
		Node node = createNode();
		NodeEvent event = createNodeEvent(node);

		assertEquals(event.toString(),
			Utils.dateToString(event.getCreatedAt()) + "  " + event.getType()
				+ "  " + node);
	}
}
