package com.googlecode.icegem.cacheutils.monitor.controller.event;

import org.testng.annotations.Test;

import com.googlecode.icegem.cacheutils.monitor.controller.model.Node;
import com.googlecode.icegem.cacheutils.monitor.utils.Utils;

import static org.fest.assertions.Assertions.assertThat;

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

		assertThat(event.getNode()).isEqualTo(node);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetNodeNullNode() {
		new NodeEvent(null, NodeEventType.ADDED);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetNodeNullEventType() {
		Node node = createNode();
		new NodeEvent(node, null);
	}

	@Test
	public void testGetType() {
		Node node = createNode();
		NodeEvent event = createNodeEvent(node);

		assertThat(event.getType()).isEqualTo(NodeEventType.ADDED);
	}

	@Test
	public void testGetCreatedAt() {
		long testStartTime = System.currentTimeMillis();

		Node node = createNode();
		NodeEvent event = createNodeEvent(node);

		assertThat(event.getCreatedAt()).isGreaterThanOrEqualTo(testStartTime);
	}

	@Test
	public void testToString() {
		Node node = createNode();
		NodeEvent event = createNodeEvent(node);

		assertThat(event.toString()).isEqualTo(
			Utils.dateToString(event.getCreatedAt()) + "  " + event.getType()
				+ "  " + node);
	}
}
