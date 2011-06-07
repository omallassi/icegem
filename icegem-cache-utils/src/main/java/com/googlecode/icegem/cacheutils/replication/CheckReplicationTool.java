package com.googlecode.icegem.cacheutils.replication;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.googlecode.icegem.cacheutils.Tool;
import com.googlecode.icegem.cacheutils.monitor.utils.Utils;

/**
 * The main class of the replication measurement tool. It parses command-line
 * options, configures and creates the guest nodes, and collects their
 * responses.
 * 
 * Responses with exit code 0 in case of all the relations established, 1
 * otherwise.
 */
public class CheckReplicationTool extends Tool {

	/* Name of timeout option */
	private static final String TIMEOUT_OPTION = "timeout";

	/* Name of locators option */
	private static final String CLUSTERS_OPTION = "clusters";

	/* Name of region option */
	private static final String REGION_OPTION = "region";

	/* Name of license file option */
	private static final String LICENSE_FILE_OPTION = "license-file";

	private static final String LICENSE_TYPE_OPTION = "license-type";

	/* Name of help option */
	private static final String HELP_OPTION = "help";

	/* Default timeout is 1 minute */
	private static final long DEFAULT_TIMEOUT = 60 * 1000;

	/* Additional timeout */
	private static final long DELTA_TIMEOUT = 10 * 1000;

	/* Default license file is gemfireLicense.zip */
	private static final String DEFAULT_LICENSE_FILE_PATH = null;

	private static final String DEFAULT_LICENSE_TYPE = "evaluation";

	/* Default region name is "proxy" */
	private static final String DEFAULT_REGION_NAME = "proxy";

	private static final boolean DEFAULT_DEBUG_ENABLED = false;

	private static final String DEBUG_OPTION = "debug";

	/* Waiting timeout */
	private static long timeout = DEFAULT_TIMEOUT;

	/* Path to the license file */
	private static String licenseFilePath = DEFAULT_LICENSE_FILE_PATH;

	private static String licenseType = DEFAULT_LICENSE_TYPE;

	/* Technical region name */
	private static String regionName = DEFAULT_REGION_NAME;

	private Properties clustersProperties;

	private boolean debugEnabled = DEFAULT_DEBUG_ENABLED;

	private class ProcessorTask implements Runnable {

		private int exitCode;
		private final Properties clustersProperties;
		private final long timeout;
		private final String licenseFilePath;
		private final String regionName;
		private final String licenseType;
		private final boolean debugEnabled;

		public ProcessorTask(Properties clustersProperties, long timeout,
			String licenseFilePath, String licenseType, String regionName, boolean debugEnabled) {
			this.clustersProperties = clustersProperties;
			this.timeout = timeout;
			this.licenseFilePath = licenseFilePath;
			this.licenseType = licenseType;
			this.regionName = regionName;
			this.debugEnabled = debugEnabled;
		}

		public void run() {
			ReplicationProcessor processor = new ReplicationProcessor(
				clustersProperties, timeout, licenseFilePath, licenseType,
				regionName, debugEnabled);

			exitCode = 1;
			try {
				exitCode = processor.process();
			} catch (Throwable t) {
				debug(
					"CheckReplicationTool.ProcessorTask#run(): Throwable caught with message = "
						+ t.getMessage(), t);
			}

		}

		public int getExitCode() {
			return exitCode;
		}
	}

	/**
	 * Runs the tool. All the tools run in this way.
	 */
	public void execute(String[] args) {
		try {
			debug("CheckReplicationTool#execute(String[]): args = "
				+ Arrays.asList(args));

			parseCommandLineArguments(args);

			debug("CheckReplicationTool#execute(String[]): Creating CheckReplicationTool.ProcessorTask with parameters: clustersProperties = "
				+ clustersProperties
				+ ", timeout = "
				+ timeout
				+ ", licenseFilePath = "
				+ licenseFilePath
				+ ", licenseType = "
				+ licenseType + ", regionName = " + regionName);

			ProcessorTask task = new ProcessorTask(clustersProperties, timeout,
				licenseFilePath, licenseType, regionName, debugEnabled);

			debug("CheckReplicationTool#execute(String[]): Starting CheckReplicationTool.ProcessorTask");

			Utils.execute(task, timeout + DELTA_TIMEOUT);

			int exitCode = task.getExitCode();

			debug("CheckReplicationTool#execute(String[]): CheckReplicationTool.ProcessorTask finished with exitCode = "
				+ exitCode);

			System.exit(exitCode);
		} catch (Throwable t) {
			debug(
				"CheckReplicationTool#execute(String[]): Throwable caught with message = "
					+ t.getMessage(), t);

			System.exit(1);
		}
	}

	/**
	 * Parses command-line arguments and sets the local variables
	 * 
	 * @param commandLineArguments
	 *            - the list of command-line arguments
	 */
	protected void parseCommandLineArguments(String[] commandLineArguments) {
		Options options = constructGnuOptions();

		if (commandLineArguments.length < 1) {
			printHelp(options);
		}

		CommandLineParser parser = new GnuParser();
		try {
			CommandLine line = parser.parse(options, commandLineArguments);

			if (line.hasOption(HELP_OPTION)) {
				printHelp(options);
			}

			if (line.hasOption(DEBUG_OPTION)) {
				debugEnabled = true;
			}
			
			if (line.hasOption(LICENSE_FILE_OPTION)) {
				licenseFilePath = line.getOptionValue(LICENSE_FILE_OPTION);
			}

			if (line.hasOption(LICENSE_TYPE_OPTION)) {
				licenseType = line.getOptionValue(LICENSE_TYPE_OPTION);
			}

			if (line.hasOption(REGION_OPTION)) {
				regionName = line.getOptionValue(REGION_OPTION);
			}

			if (line.hasOption(TIMEOUT_OPTION)) {
				String timeoutString = line.getOptionValue(TIMEOUT_OPTION);
				timeout = Long.parseLong(timeoutString);
			}

			if (line.hasOption(CLUSTERS_OPTION)) {
				clustersProperties = line.getOptionProperties(CLUSTERS_OPTION);
			} else {
				printHelp(options);
			}

		} catch (Throwable t) {
			printHelp(options);
		}
	}

	/**
	 * Prints help if requested, or in case of any misconfiguration
	 * 
	 * @param options
	 *            - the GNU options
	 */
	protected void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("check-replication", options);

		System.exit(1);
	}

	/**
	 * Constructs the set of GNU options
	 * 
	 * @return - the constructed options
	 */
	protected Options constructGnuOptions() {
		final Options gnuOptions = new Options();

		gnuOptions
			.addOption("t", TIMEOUT_OPTION, true, "Timeout, ms")
			.addOption("lf", LICENSE_FILE_OPTION, true,
				"The path to non default license file")
			.addOption("lt", LICENSE_TYPE_OPTION, true, "The type of license")
			.addOption(
				"r",
				REGION_OPTION,
				true,
				"The name of region for this test. Default name is \""
					+ DEFAULT_REGION_NAME + "\"")
			.addOption("d", DEBUG_OPTION, false, "Print debug information")
			.addOption("h", HELP_OPTION, false, "Print usage information");

		@SuppressWarnings("static-access")
		Option locatorsOption = OptionBuilder
			.hasArgs()
			.withDescription(
				"Clusters and its locators of GemFire system. Example: -c cluster1=host1[port1],host2[port2] -c cluster2=host3[port3]")
			.withValueSeparator().withArgName("cluster=locators")
			.withLongOpt(CLUSTERS_OPTION).create("c");

		gnuOptions.addOption(locatorsOption);

		return gnuOptions;
	}

	private void debug(String message) {
		debug(message, null);
	}

	private void debug(String message, Throwable t) {
		if (debugEnabled) {
			System.err.println(message);
			
			if (t != null) {
				t.printStackTrace(System.err);
			}
		}
	}
}
