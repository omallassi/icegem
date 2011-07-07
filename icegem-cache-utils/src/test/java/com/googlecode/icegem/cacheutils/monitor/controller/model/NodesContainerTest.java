package com.googlecode.icegem.cacheutils.monitor.controller.model;

import java.util.Set;

import org.junit.Test;

import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;

import static org.junit.Assert.*;

public class NodesContainerTest {

	private NodesContainer createNodesContainer() {
		NodesContainer container = new NodesContainer();

		Node aliveNode = new Node("127.0.0.1", 40404, null);
		aliveNode.markAsAlive();
		container.add(aliveNode);

		Node deadNode = new Node("127.0.0.1", 40405, null);
		deadNode.markAsDead();
		container.add(deadNode);
		container.add(new Node("192.168.1.100", 40404, null));

		return container;
	}

	@Test
	public void testFindByHostAndPort() {
		NodesContainer container = createNodesContainer();

		final String host = "127.0.0.1";
		final int port = 40405;

		Node node = container.find(host, port);

		assertNotNull(node);
		assertEquals(node.getHost(), host);
		assertEquals(node.getPort(), port);
	}

	@Test
	public void testFindByHost() {
		NodesContainer container = createNodesContainer();

		final String host = "127.0.0.1";

		Set<Node> nodesSet = container.find(host);

		assertNotNull(nodesSet);
		assertEquals(nodesSet.size(), 2);
		for (Node node : nodesSet) {
			assertEquals(node.getHost(), host);
		}
	}

	@Test
	public void testGetAll() {
		NodesContainer container = createNodesContainer();

		Set<Node> allNodesSet = container.getAll();

		assertNotNull(allNodesSet);
		assertEquals(allNodesSet.size(), 3);
	}

	@Test
	public void testGetAllNotDead() {
		NodesContainer container = createNodesContainer();

		Set<Node> notDeadNodesSet = container.getAllNotDead();

		assertNotNull(notDeadNodesSet); 
		assertEquals(notDeadNodesSet.size(), 2);
		for (Node node : notDeadNodesSet) {
			assertFalse(node.getStatus() == NodeStatus.DEAD);
		}
	}

	@Test
	public void testGetAllDead() {
		NodesContainer container = createNodesContainer();

		Set<Node> deadNodesSet = container.getAllDead();

		assertNotNull(deadNodesSet); 
		assertEquals(deadNodesSet.size(), 1);
		for (Node node : deadNodesSet) {
			assertEquals(node.getStatus(), NodeStatus.DEAD);
		}
	}

	@Test
	public void testAdd() {
		NodesContainer container = createNodesContainer();

		container.add(new Node("192.168.0.1", 40404, null));

		Set<Node> allNodesSet = container.getAll();

		assertNotNull(allNodesSet); 
		assertEquals(allNodesSet.size(), 4);
	}

	@Test
	public void testRemove() {
		NodesContainer container = createNodesContainer();

		Node node = container.getAll().iterator().next();
		container.remove(node);

		Set<Node> allNodesSet = container.getAll();

		assertNotNull(allNodesSet);
		assertEquals(allNodesSet.size(), 2);
	}

	@Test
	public void testMarkAsAlive() {
		NodesContainer container = createNodesContainer();

		for (Node node : container.getAll()) {
			container.markAsAlive(node);
		}

		Set<Node> allNodesSet = container.getAll();

		assertNotNull(allNodesSet);
		assertEquals(allNodesSet.size(), 3);
		for (Node node : allNodesSet) {
			assertEquals(node.getStatus(), NodeStatus.ALIVE);
		}
	}

	@Test
	public void testMarkAsDead() {
		NodesContainer container = createNodesContainer();

		for (Node node : container.getAll()) {
			container.markAsDead(node);
		}

		Set<Node> allNodesSet = container.getAll();

		assertNotNull(allNodesSet);
		assertEquals(allNodesSet.size(), 3);
		for (Node node : allNodesSet) {
			assertEquals(node.getStatus(), NodeStatus.DEAD);
		}
	}

	private class CountingNodeEventHandler implements NodeEventHandler {

		private int handledEventsCount = 0;

		public void handle(NodeEvent event) {
			handledEventsCount++;
		}

		public int getHandledEventsCount() {
			return handledEventsCount;
		}
	}

	@Test
	public void testAddNodeEventHandler() {
		NodesContainer container = createNodesContainer();

		CountingNodeEventHandler handler = new CountingNodeEventHandler();

		container.addNodeEventHandler(handler);

		Node node = new Node("192.168.0.1", 40404, null);
		container.add(node);
		container.markAsAlive(node);
		container.markAsDead(node);
		container.remove(node);

		assertEquals(handler.getHandledEventsCount(), 4);
	}

}
