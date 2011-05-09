package com.googlecode.icegem.cacheutils.monitor;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import com.gemstone.gemfire.admin.AdminDistributedSystem;
import com.gemstone.gemfire.admin.AdminException;
import com.gemstone.gemfire.admin.SystemMember;
import com.gemstone.gemfire.admin.SystemMemberCache;
import com.gemstone.gemfire.admin.SystemMemberCacheServer;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolFactory;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedSystem;
import com.gemstone.gemfire.internal.cache.execute.DefaultResultCollector;
import com.googlecode.icegem.cacheutils.common.AdminService;
import com.googlecode.icegem.cacheutils.monitor.function.ZeroFunction;
import com.googlecode.icegem.cacheutils.monitor.utils.EmailService;
import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;

/**
 * Periodically checks the distributed system status and send mail in case of
 * failure
 */
public class MonitoringTool {

	private DistributedSystem connection;
	private AdminDistributedSystem adminDistributedSystem;
	private PropertiesHelper propertiesHelper;

	/**
	 * Periodically running task which checks the system status
	 */
	private class IsAliveTimerTask extends TimerTask {

		@Override
		public void run() {
			try {
				boolean alive = isAlive();
				System.err.println("alive = " + alive);
				if (!alive) {
					EmailService
							.getInstance()
							.send(propertiesHelper
									.getStringProperty("com.googlecode.icegem.cacheutils.monitor.email.alert.subject"),
									propertiesHelper
											.getStringProperty(
													"com.googlecode.icegem.cacheutils.monitor.email.alert.content",
													alive));
				}
			} catch (Throwable t) {
				System.out.println("Throwable catched: " + t.getMessage());
				try {
					EmailService
							.getInstance()
							.send(propertiesHelper
									.getStringProperty("com.googlecode.icegem.cacheutils.monitor.email.exception.subject"),
									propertiesHelper
											.getStringProperty(
													"com.googlecode.icegem.cacheutils.monitor.email.exception.content",
													t.getMessage()));
				} catch (MessagingException me) {
					me.printStackTrace();
				}
			}
		}

	}

	/**
	 * Prepares and starts task configured using property files
	 * 
	 * @throws Exception
	 */
	public MonitoringTool() throws Exception {
		propertiesHelper = new PropertiesHelper("monitoring.properties");

		adminDistributedSystem = new AdminService(
				propertiesHelper
						.getStringProperty("com.googlecode.icegem.cacheutils.monitor.locators"))
				.getAdmin();

		Timer timer = new Timer();
		timer.schedule(
				new IsAliveTimerTask(),
				propertiesHelper
						.getLongProperty("com.googlecode.icegem.cacheutils.monitor.timer.delay"),
				propertiesHelper
						.getLongProperty("com.googlecode.icegem.cacheutils.monitor.timer.period"));
	}

	/**
	 * Checks the system status
	 * 
	 * @return true in case of the system is alive
	 * @throws AdminException
	 * @throws FunctionException
	 * @throws InterruptedException
	 */
	private boolean isAlive() throws AdminException, FunctionException,
			InterruptedException {
		boolean alive = true;

		SystemMember[] systemMemberApplications = adminDistributedSystem
				.getSystemMemberApplications();
		PoolFactory poolFactory = PoolManager.createFactory();
		for (SystemMember member : systemMemberApplications) {
			String host = member.getHost();
			int port = extractPort(member);
			if (port > -1) {
				Pool pool = findPool(poolFactory, host, port);
				int zero = executeZeroFunction(
						pool,
						propertiesHelper
								.getLongProperty("com.googlecode.icegem.cacheutils.monitor.function.timeout"));
				
				if (zero != 0) {
					alive = false;
					break;
				}
			}
		}

		return alive;
	}

	/**
	 * Executes the "zero" function using pool for the concrete server. Will
	 * stop waiting the function execution after timeout.
	 * 
	 * @param pool
	 *            - the pool for the concrete server
	 * @param timeout
	 *            - the function execution timeout
	 * @return 0 in case of function executed without problems, -1 otherwise
	 * @throws FunctionException
	 * @throws InterruptedException
	 */
	private int executeZeroFunction(Pool pool, long timeout)
			throws FunctionException, InterruptedException {
		int result = -1;
		FunctionService.registerFunction(new ZeroFunction());
		Execution execution = FunctionService.onServer(pool);
		execution.withCollector(new DefaultResultCollector());
		ResultCollector collector = execution
				.execute("com.googlecode.icegem.cacheutils.monitor.function.ZeroFunction");
		List functionResult = (List) collector.getResult(timeout,
				TimeUnit.MILLISECONDS);
		if ((functionResult != null) && (functionResult.size() == 1)
				&& (functionResult.get(0) instanceof Integer)) {
			result = (Integer) functionResult.get(0);
		}

		return result;
	}

	/**
	 * Looks for the existing pool with name poolFactory. If there is no such
	 * pool, creates new one.
	 * 
	 * @param poolFactory
	 *            - the PoolFactory instance
	 * @param host
	 *            - the concrete server's host
	 * @param port
	 *            - the concrete server's port
	 * @return found or new pool for the specified parameters
	 */
	private Pool findPool(PoolFactory poolFactory, String host, int port) {
		String poolName = "pool-" + host + "-" + port;
		Pool pool = PoolManager.find(poolName);
		if (pool == null) {
			poolFactory.reset();
			poolFactory.addServer(host, port);
			pool = poolFactory.create(poolName);
		}

		return pool;
	}

	/**
	 * Extracts port from the SystemMember object.
	 * 
	 * @param member
	 *            - the specified SystemMember
	 * @return - port if its found, -1 otherwise
	 * @throws AdminException
	 */
	private int extractPort(SystemMember member) throws AdminException {
		int port = -1;
		SystemMemberCache cache = member.getCache();
		if (cache != null) {
			SystemMemberCacheServer[] cacheServers = cache.getCacheServers();
			if ((cacheServers != null) && (cacheServers.length == 1)) {
				port = cacheServers[0].getPort();
			}
		}

		return port;
	}

	/**
	 * Starts the monitoring tool
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new MonitoringTool();
	}

}
