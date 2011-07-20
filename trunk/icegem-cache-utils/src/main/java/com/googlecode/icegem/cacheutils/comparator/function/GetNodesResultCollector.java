package com.googlecode.icegem.cacheutils.comparator.function;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.googlecode.icegem.cacheutils.comparator.model.Node;

public class GetNodesResultCollector implements
	ResultCollector<Serializable, Serializable> {

	private Semaphore lock = new Semaphore(1);
	private Map<Long, Node> idToNodeMap = new HashMap<Long, Node>();

	public void addResult(DistributedMember member, Serializable value) {
		try {
			lock.acquire();

			if (value instanceof Node[]) {
				Node[] nodes = (Node[]) value;
				
				for (Node node : nodes) {
					Node registeredNode = idToNodeMap.get(node.getId());
					if (registeredNode == null) {
						registeredNode = node;
					} else {
						registeredNode.merge(node);
					}
					idToNodeMap.put(registeredNode.getId(), registeredNode);
				}
			}

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}

	}

	public void clearResults() {
		try {
			lock.acquire();

			idToNodeMap = new HashMap<Long, Node>();

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}
	}

	public void endResults() {
	}

	public Serializable getResult() throws FunctionException {
		try {
			lock.acquire();

			return prepareResult();

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}
	}

	public Serializable getResult(long timeout, TimeUnit timeUnit)
		throws FunctionException, InterruptedException {
		try {
			if (!lock.tryAcquire(timeout, timeUnit)) {
				throw new FunctionException("Timeout during the lock acquiring");
			}

			return prepareResult();

		} catch (InterruptedException e) {
			throw new FunctionException(e);
		} finally {
			lock.release();
		}
	}
	
	private HashSet<Node> prepareResult() {
		return new HashSet<Node>(idToNodeMap.values());
	}
}
