package com.googlecode.icegem.cacheutils.replication.relations;

/**
 * Represents relation state between two guest nodes - local and remote
 */
public enum RelationState {
	/* Relation is new */
	NEW,

	/* Local guest node is connected to remote guest node */
	CONNECTED;
}
