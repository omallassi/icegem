package com.googlecode.icegem.cacheutils.replication.relations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RelationsController {

	private String localDistributedSystem;
	private Set<Relation> relationsSet = new HashSet<Relation>();

	public RelationsController(String localDistributedSystem,
		Set<String> remoteDistributedSystemsSet) {
		this.localDistributedSystem = localDistributedSystem;

		for (String remoteDistributedSystem : remoteDistributedSystemsSet) {
			add(new Relation(remoteDistributedSystem, localDistributedSystem));
		}
	}

	private void add(Relation relation) {
		relationsSet.add(relation);
	}

	public Relation get(String from) {
		Relation result = null;

		for (Relation relation : relationsSet) {
			if (from.equals(relation.getFrom())
				&& localDistributedSystem.equals(relation.getTo())) {
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

			sb.append("Replicated to ").append(localDistributedSystem)
				.append(" from ");

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
				localDistributedSystem);
		}

		return sb.toString();
	}
}
