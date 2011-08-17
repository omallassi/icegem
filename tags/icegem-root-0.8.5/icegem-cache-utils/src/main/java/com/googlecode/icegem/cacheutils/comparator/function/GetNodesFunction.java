package com.googlecode.icegem.cacheutils.comparator.function;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.googlecode.icegem.cacheutils.common.Utils;
import com.googlecode.icegem.cacheutils.comparator.model.Node;

public class GetNodesFunction extends FunctionAdapter implements Declarable {

	private static final long serialVersionUID = -6448375948152121283L;

	private static final int BASE_MULTIPLIER = 1;

	public void init(Properties arg0) {
		// do nothing
	}

	public void execute(FunctionContext functionContext) {
		ResultSender<Serializable> resultSender = functionContext
			.getResultSender();

		try {
			RegionFunctionContext context = (RegionFunctionContext) functionContext;

			GetNodesFunctionArguments arguments = (GetNodesFunctionArguments) context
				.getArguments();

			Utils.registerClasses(arguments.getPackages());

			Region<?, ?> region = context.getDataSet();

			if (PartitionRegionHelper.isPartitionedRegion(region)) {
				region = PartitionRegionHelper.getLocalData(region);
			}

			Node[] nodes = getNodes(region, arguments.getLoadFactor(),
				arguments.getIds(), arguments.getShift());

			resultSender.lastResult(nodes);
		} catch (Throwable t) {
			resultSender.sendException(t);
		}
	}

	public Node[] getNodes(Region<?, ?> region, Integer loadFactor, long[] ids,
		int shift) {

		Map<Long, Node> idToNodeMap = createIdToNodeMap(ids);

		final long baseMask = shift > 63 ? 0 : 0xFFFFFFFFFFFFFFFFL << shift;
		final long childrenBaseMask = 0xFFFFFFFFFFFFFFFFL << (shift - 16);

		long lastWakeUpTime = System.currentTimeMillis();
		for (Object key : region.keySet()) {
			Object value = region.get(key);

			long entryHashcode = calculateEntryHashcode(key, value);
			long id = entryHashcode & baseMask;
			long childId = entryHashcode & childrenBaseMask;

			Node node = idToNodeMap.get(id);
			if (node != null) {
				Node newNode = new Node(id);
				Node child = new Node(childId);
				child.addHashcode(entryHashcode);
				if (childrenBaseMask == 0xFFFFFFFFFFFFFFFFL) {
					child.setData(key);
				}
				newNode.addChild(child);
				newNode.addHashcode(entryHashcode);
				
				node.merge(newNode);
				
				idToNodeMap.put(id, node);
			}

			lastWakeUpTime = sleep(lastWakeUpTime, loadFactor);
		}

		return mapToNodeArray(idToNodeMap);
	}

	private Map<Long, Node> createIdToNodeMap(long[] ids) {
		Map<Long, Node> result = new HashMap<Long, Node>();

		for (long id : ids) {
			result.put(id, new Node(id));
		}

		return result;
	}

	private Node[] mapToNodeArray(Map<Long, Node> map) {
		Collection<Node> values = map.values();
		return values.toArray(new Node[values.size()]);
	}

	private long calculateEntryHashcode(Object key, Object value) {
		int keyHashcode = key.hashCode();
		int valueHashcode = value.hashCode();

		return (((long) valueHashcode) << 32) + keyHashcode;
	}

	private long sleep(long lastWakeUpTime, Integer loadFactor) {

		long nextSleepTime = lastWakeUpTime + BASE_MULTIPLIER * loadFactor;
		long currentTime = System.currentTimeMillis();

		boolean hasSleep = false;
		if (currentTime > nextSleepTime) {
			try {
				Thread.sleep(BASE_MULTIPLIER * (100 - loadFactor));
			} catch (InterruptedException e) {
			}

			hasSleep = true;
		}

		long newLastWakeUpTime;
		if (hasSleep) {
			newLastWakeUpTime = System.currentTimeMillis();
		} else {
			newLastWakeUpTime = lastWakeUpTime;
		}

		return newLastWakeUpTime;
	}

	@Override
	public String getId() {
		return this.getClass().getName();
	}

	@Override
	public boolean isHA() {
		return false;
	}
}
