package com.googlecode.icegem.cacheutils.monitor.controller.model;

/**
 * All the possible statuses of a node.
 */
public enum NodeStatus {
	/* The node has been added but not checked yet */
	NEW,

	/* The node has been checked and it is alive */
	ALIVE,

	/* The node has been checked and it is dead */
	DEAD;
}
