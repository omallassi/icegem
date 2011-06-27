package com.googlecode.icegem.cacheutils.monitor.controller.event;

/**
 * The list of possible node event types.
 */
public enum NodeEventType {
	/* The node has been added */
	ADDED,

	/* The node has been marked as ALIVE */
	MARKED_AS_ALIVE,

	/* The node has been marked as DEAD */
	MARKED_AS_DEAD,

	/* The node has been removed */
	REMOVED;
}
