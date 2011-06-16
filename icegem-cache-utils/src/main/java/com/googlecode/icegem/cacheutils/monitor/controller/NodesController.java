package com.googlecode.icegem.cacheutils.monitor.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import com.gemstone.gemfire.admin.AdminException;
import com.gemstone.gemfire.admin.OperationCancelledException;
import com.gemstone.gemfire.admin.SystemMember;
import com.gemstone.gemfire.admin.SystemMemberCache;
import com.gemstone.gemfire.admin.SystemMemberCacheServer;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolFactory;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.googlecode.icegem.cacheutils.common.AdminService;
import com.googlecode.icegem.cacheutils.common.Utils;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;
import com.googlecode.icegem.cacheutils.monitor.controller.model.Node;
import com.googlecode.icegem.cacheutils.monitor.controller.model.NodesContainer;
import com.googlecode.icegem.cacheutils.monitor.utils.EmailService;
import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;

public class NodesController {

	private NodesContainer nodes = new NodesContainer();
	private AdminService adminService;
	private PropertiesHelper propertiesHelper;
	private PoolFactory poolFactory;
	private BufferedNodeEventHandler bufferedNodeEventHandler;
	private long timeout;

	public NodesController(PropertiesHelper propertiesHelper, String locators,
		long timeout) throws Exception {

		this.propertiesHelper = propertiesHelper;
		this.timeout = timeout;

		adminService = new AdminService(locators, false);

		poolFactory = PoolManager.createFactory();
		bufferedNodeEventHandler = new BufferedNodeEventHandler();
		nodes.addNodeEventHandler(bufferedNodeEventHandler);
	}

	/**
	 * Extracts port from the SystemMember object.
	 * 
	 * @param member
	 *            - the specified SystemMember
	 * @return - port if its found, -1 otherwise
	 * @throws AdminException
	 */
	private Set<Integer> extractPortsSet(SystemMember member)
		throws AdminException {
		Set<Integer> portsSet = new HashSet<Integer>();
		SystemMemberCache cache = member.getCache();
		if (cache != null) {
			SystemMemberCacheServer[] cacheServers = cache.getCacheServers();
			if (cacheServers != null) {
				for (SystemMemberCacheServer cacheServer : cacheServers) {
					portsSet.add(cacheServer.getPort());
				}
			}
		}

		return portsSet;
	}

	/**
	 * Looks for the existing pool with name poolFactory. If there is no such
	 * pool, creates new one.
	 * 
	 * @param host
	 *            - the concrete server's host
	 * @param port
	 *            - the concrete server's port
	 * @return found or new pool for the specified parameters
	 */
	private Pool findOrCreatePool(String host, int port) {
		String poolName = Utils.toKey(host, port);
		Pool pool = PoolManager.find(poolName);
		if (pool == null) {
			poolFactory.reset();
			poolFactory.addServer(host, port);
			pool = poolFactory.create(poolName);
		}

		return pool;
	}

	private void detectNewNodes() throws AdminException {
		SystemMember[] systemMemberApplications = adminService.getAdmin()
			.getSystemMemberApplications();

		for (SystemMember member : systemMemberApplications) {
			String host = member.getHost();
			try {
				Set<Integer> portsSet = extractPortsSet(member);
				for (int port : portsSet) {
					Node node = nodes.find(host, port);
					if (node == null) {
						Pool pool = findOrCreatePool(host, port);
						node = new Node(host, port, pool);
						nodes.add(node);
					}
				}
			} catch (OperationCancelledException oce) {
				// do nothing
			}
		}
	}

	private boolean isOperable(Pool pool) {
		boolean operable = false;

		FunctionExecutionThread functionExecutionThread = new FunctionExecutionThread(
			pool);
		Utils.execute(functionExecutionThread, timeout);
		int zero = functionExecutionThread.getZero();

		if (zero == 0) {
			operable = true;
		}

		return operable;
	}

	private void detectDeadNodes() {
		for (Node node : nodes.getAll()) {
			boolean operable = isOperable(node.getPool());

			if (operable) {
				nodes.markAsAlive(node);
			} else {
				nodes.markAsDead(node);
			}
		}
	}

	private void processDeadNodes() {
		for (Node node : nodes.getAllDead()) {
			boolean socketAlive = Utils.isSocketAlive(node.getHost(),
				node.getPort());

			if (!socketAlive) {
				nodes.remove(node);
			}
		}
	}

	private void sendAlertEmail() throws MessagingException {
		List<NodeEvent> eventsList = bufferedNodeEventHandler
			.getAndClearEventslist();

		// send alert email
		if (eventsList.size() > 0) {
			EmailService
				.getInstance()
				.send(
					propertiesHelper
						.getStringProperty("icegem.cacheutils.monitor.email.alert.subject"),
					propertiesHelper.getStringProperty(
						"icegem.cacheutils.monitor.email.alert.content",
						toContentStringHTML(eventsList), Utils.currentDate()));
		}
	}

	private String toContentStringHTML(List<NodeEvent> eventslist) {
		StringBuilder sb = new StringBuilder();

		sb.append("<p>");
		sb.append("<table style=\"border-collapse:collapse; font: 14px Georgia;\" cellpadding=\"10\" border=\"1\">");
		sb.append("<tr style=\"background: #f0f0f0; text-align: left;\">");
		sb.append("<th>Event date</th>");
		sb.append("<th>Event type</th>");
		sb.append("<th>Node</th>");
		sb.append("</tr>");
		for (NodeEvent event : eventslist) {
			sb.append("<tr>");
			sb.append("<td>").append(Utils.dateToString(event.getCreatedAt()))
				.append("</td>");
			sb.append("<td>").append(event.getType()).append("</td>");
			sb.append("<td>").append(event.getNode()).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</p>");

		return sb.toString();
	}

	public void update() throws AdminException, MessagingException {
		detectNewNodes();
		detectDeadNodes();
		processDeadNodes();
		sendAlertEmail();
	}

	public boolean isServerAlive(String host, int port) {
		Pool pool = findOrCreatePool(host, port);
		return isOperable(pool);
	}

	public void addNodeEventHandler(NodeEventHandler handler) {
		nodes.addNodeEventHandler(handler);
	}

	public void shutdown() {
		adminService.close();
	}
}
