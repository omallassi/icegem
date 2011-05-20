package com.googlecode.icegem.cacheutils.monitor;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.googlecode.icegem.cacheutils.monitor.controller.NodesController;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;
import com.googlecode.icegem.cacheutils.monitor.utils.EmailService;
import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;
import com.googlecode.icegem.cacheutils.monitor.utils.Utils;

/**
 * Periodically checks the distributed system status and sends mail in case of
 * failure
 */
public class MonitoringTool {
	private static final Logger log = Logger.getLogger(MonitoringTool.class);
	
	private NodesController nodesController;
	private PropertiesHelper propertiesHelper;
	private Timer timer;

	/**
	 * Periodically running task which checks the system status
	 */
	private class IsAliveTimerTask extends TimerTask {

		@Override
		public void run() {
			try {
				nodesController.update();
			} catch (Throwable t) {
				log.error(Utils.currentDate() + "  Throwable catched", t);
				t.printStackTrace();
				try {
					EmailService
						.getInstance()
						.send(
							propertiesHelper
								.getStringProperty("icegem.cacheutils.monitor.email.exception.subject"),
							propertiesHelper
								.getStringProperty(
									"icegem.cacheutils.monitor.email.exception.content",
									t.getMessage(), new Date()));
				} catch (MessagingException me) {
					me.printStackTrace();
				}
			}

		}

	}

	/**
	 * Creates and configures the tool
	 * 
	 * @throws Exception
	 */
	public MonitoringTool() throws Exception {
		log.info(Utils.currentDate() + "");
		log.info(Utils.currentDate() + "  --------------------------------------------------");
		log.info(Utils.currentDate() + "  Monitoring tool started");
		log.info(Utils.currentDate() + "  --------------------------------------------------");

		propertiesHelper = new PropertiesHelper("/monitoring.properties");
		
		nodesController = new NodesController(propertiesHelper);
		nodesController.addNodeEventHandler(new LoggerNodeEventHandler());
		
		timer = new Timer();
	}

	/**
	 * Starts the checking task 
	 */
	public void start() {
		timer
			.schedule(
				new IsAliveTimerTask(),
				propertiesHelper
					.getLongProperty("icegem.cacheutils.monitor.timer.delay"),
				propertiesHelper
					.getLongProperty("icegem.cacheutils.monitor.timer.period"));
	}

	public void addNodeEventHandler(NodeEventHandler handler) {
		nodesController.addNodeEventHandler(handler);
	}
	
	/**
	 * Starts the monitoring tool
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		MonitoringTool monitoringTool = new MonitoringTool();
		monitoringTool.start();
	}

}
