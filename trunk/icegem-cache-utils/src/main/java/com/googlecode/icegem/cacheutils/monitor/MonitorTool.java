package com.googlecode.icegem.cacheutils.monitor;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.googlecode.icegem.cacheutils.Tool;
import com.googlecode.icegem.cacheutils.monitor.controller.NodesController;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;
import com.googlecode.icegem.cacheutils.monitor.utils.EmailService;
import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;
import com.googlecode.icegem.cacheutils.monitor.utils.Utils;

/**
 * Periodically checks the distributed system status and sends mail in case of
 * failure
 */
public class MonitorTool extends Tool {
	private static final Logger log = Logger.getLogger(MonitorTool.class);

	private static boolean allOption;
	private static String serverHostOption;
	private static int serverPortOption;

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
	 * Creates
	 * 
	 * @throws Exception
	 */
	public MonitorTool() {

	}

	/**
	 * configuration
	 * */
	public void init() {
		log.info(Utils.currentDate() + "");
		log.info(Utils.currentDate()
			+ "  --------------------------------------------------");
		log.info(Utils.currentDate() + "  Monitoring tool started");
		log.info(Utils.currentDate()
			+ "  --------------------------------------------------");

		try {
			propertiesHelper = new PropertiesHelper("/monitoring.properties");
		} catch (IOException e) {
			throw new RuntimeException(
				"error reading properties \'/monitoring.properties\' ", e);
		}

		try {
			nodesController = new NodesController(propertiesHelper, true);
		} catch (Exception e) {
			throw new RuntimeException("error creating NodesController", e);
		}
		nodesController.addNodeEventHandler(new LoggerNodeEventHandler());

		timer = new Timer();
	}

	/**
	 * Starts the checking task
	 */
	public void start() {
		timer.schedule(new IsAliveTimerTask(), propertiesHelper
			.getLongProperty("icegem.cacheutils.monitor.timer.delay"),
			propertiesHelper
				.getLongProperty("icegem.cacheutils.monitor.timer.period"));
	}

	public void shutdown() {
		nodesController.shutdown();

		timer.cancel();
		timer = null;
	}

	public static boolean isServerAlive(String host, int port) throws Exception {
		PropertiesHelper propertiesHelper = new PropertiesHelper(
			"/monitoring.properties");

		NodesController nodesController = new NodesController(propertiesHelper,
			false);

		boolean serverAlive = nodesController.isServerAlive(host, port);

		nodesController.shutdown();

		return serverAlive;
	}

	public void addNodeEventHandler(NodeEventHandler handler) {
		nodesController.addNodeEventHandler(handler);
	}

	public void execute(String[] args) {
		parseCommandLineArguments(args);

		if (serverHostOption != null) {
			boolean serverAlive = false;
			try {
				serverAlive = MonitorTool.isServerAlive(serverHostOption,
					serverPortOption);
			} catch (Exception e) {
				throw new RuntimeException("error in checking server liveness",
					e);
			}

			int status;
			if (serverAlive) {
				status = 0;

				System.out.println("alive");
			} else {
				status = 1;

				System.out.println("down");
			}

			System.exit(status);
		} else if (allOption) {
			MonitorTool monitoringTool = null;
			try {
				monitoringTool = new MonitorTool();
				monitoringTool.init();
			} catch (Exception e) {
				throw new RuntimeException(
					"error in creating monitoring tool object. ", e);
			}
			monitoringTool.start();
		} else {
			throw new IllegalStateException("Cannot define application mode");
		}
	}

	protected void parseCommandLineArguments(String[] commandLineArguments) {
		Options options = constructGnuOptions();

		if (commandLineArguments.length < 1) {
			printHelp(options);
			System.exit(-1);
		}

		CommandLineParser parser = new GnuParser();
		try {
			CommandLine line = parser.parse(options, commandLineArguments);

			if (line.hasOption("help")) {
				printHelp(options);
				System.exit(0);
			}

			boolean allOptionTemp = line.hasOption("all");
			String serverOptionTemp = line.getOptionValue("server");

			if (serverOptionTemp != null) {
				int indexOfPortStart = serverOptionTemp.indexOf('[');
				int indexOfPortEnd = serverOptionTemp.indexOf(']');
				serverHostOption = serverOptionTemp.substring(0,
					indexOfPortStart);
				String portString = serverOptionTemp.substring(
					indexOfPortStart + 1, indexOfPortEnd);
				serverPortOption = Integer.parseInt(portString);
			} else if (allOptionTemp) {
				allOption = allOptionTemp;
			} else {
				printHelp(options);
				System.exit(-1);
			}

		} catch (Throwable t) {
			System.err
				.println("Parsing of options failed. Please check that you use correct option or specify a server in format host[port].");
			printHelp(options);
			System.exit(-1);
		}
	}

	protected void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("monitor", options);
	}

	protected Options constructGnuOptions() {
		final Options gnuOptions = new Options();

		gnuOptions
			.addOption(
				"s",
				"server",
				true,
				"Check one server and exit with status 0 if server alive, or with status 1 if server is dead or down. Server should be in format host[port].")
			.addOption(
				"a",
				"all",
				false,
				"Periodically check all the servers related to locators specified in monitoring.properties file")
			.addOption("h", "help", false, "Print usage information");

		return gnuOptions;
	}

}
