package com.googlecode.icegem.cacheutils.replication.relations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Container for relations for some guest node
 */
public class RelationsController {

	private Cluster localCluster;
	private Set<Relation> relationsSet = new HashSet<Relation>();

	public RelationsController(String localClusterName,
		Properties clustersProperties) {

		this.localCluster = new Cluster(localClusterName,
			clustersProperties.getProperty(localClusterName));

		for (Object keyObject : clustersProperties.keySet()) {
			if (localCluster.getName().equals((String) keyObject)) {
				continue;
			}

			Cluster remoteCluster = new Cluster((String) keyObject,
				(String) clustersProperties.get(keyObject));

			add(new Relation(remoteCluster, localCluster));
		}
	}

	private void add(Relation relation) {
		relationsSet.add(relation);
	}

	public Relation get(String from) {
		Relation result = null;

		for (Relation relation : relationsSet) {
			if (from.equals(relation.getFrom().getName())
				&& localCluster.equals(relation.getTo())) {
				result = relation;
				break;
			}
		}

		return result;
	}

	public boolean isConnected() {
		boolean result = true;

		for (Relation relation : relationsSet) {
			if (!RelationState.CONNECTED.equals(relation.getState())) {
				result = false;
				break;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (isConnected()) {

			sb.append("Replicated to ").append(localCluster).append(" from ");

			Iterator<Relation> it = relationsSet.iterator();
			while (it.hasNext()) {
				Relation relation = it.next();

				sb.append("[").append(relation.getFrom()).append(", ")
					.append(relation.getDuration()).append("ms]");

				if (it.hasNext()) {
					sb.append(", ");
				}
			}
		} else {
			sb.append("Connection process is not finished for ").append(
				localCluster);
		}

		return sb.toString();
	}
}
