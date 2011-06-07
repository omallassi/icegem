package com.googlecode.icegem.cacheutils.replication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
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
	private static final String LOCATORS_OPTION = "locators";

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

	/* List of clusters' locators to check */
	private static Set<String> locatorsSet;

	/* Waiting timeout */
	private static long timeout = DEFAULT_TIMEOUT;
	
	/* Path to the license file */
	private static String licenseFilePath = DEFAULT_LICENSE_FILE_PATH;

	private static String licenseType = DEFAULT_LICENSE_TYPE;

	/* Technical region name */
	private static String regionName = DEFAULT_REGION_NAME;

	private class ProcessorTask implements Runnable {

		private int exitCode;
		private final Set<String> locatorsSet;
		private final long timeout;
		private final String licenseFilePath;
		private final String regionName;
		private final String licenseType;

		public ProcessorTask(Set<String> locatorsSet, long timeout,
			String licenseFilePath, String licenseType, String regionName) {
			this.locatorsSet = locatorsSet;
			this.timeout = timeout;
			this.licenseFilePath = licenseFilePath;
			this.licenseType = licenseType;
			this.regionName = regionName;
		}

		public void run() {
			ReplicationProcessor processor = new ReplicationProcessor(
				locatorsSet, timeout, licenseFilePath, licenseType, regionName);

			exitCode = 1;
			try {
				exitCode = processor.process();
			} catch (Throwable t) {
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
			parseCommandLineArguments(args);

			ProcessorTask task = new ProcessorTask(locatorsSet, timeout,
				licenseFilePath, licenseType, regionName);

			Utils.execute(task, timeout + DELTA_TIMEOUT);

			int exitCode = task.getExitCode();

			System.exit(exitCode);
		} catch (Throwable t) {
			t.printStackTrace();
			
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

			if (line.hasOption(LOCATORS_OPTION)) {
				String locatorsCsv = line.getOptionValue(LOCATORS_OPTION);

				locatorsSet = parseLocators(locatorsCsv);
			} else {
				printHelp(options);
			}

		} catch (Throwable t) {
			printHelp(options);
		}
	}

	/**
	 * Parses CSV locators string and return set of locators
	 * 
	 * @param csvLocators
	 *            - the CSV locators string
	 * @return - the set of locators
	 * 
	 * @throws PatternSyntaxException
	 * @throws NumberFormatException
	 */
	private static Set<String> parseLocators(String csvLocators)
		throws PatternSyntaxException, NumberFormatException {

		String[] locators = csvLocators.split(",");
		List<String> locatorsList = Arrays.asList(locators);

		return new HashSet<String>(locatorsList);
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
			.addOption("l", LOCATORS_OPTION, true,
				"List of clusters' locators to check. Example: host1[port1],host2[port2]")
			.addOption("t", TIMEOUT_OPTION, true, "Timeout, ms")
			.addOption("lf", LICENSE_FILE_OPTION, true,
				"The path to non default license file")
			.addOption("lt", LICENSE_TYPE_OPTION, true,
				"The type of license")
			.addOption(
				"r",
				REGION_OPTION,
				true,
				"The name of region for this test. Default name is \""
					+ DEFAULT_REGION_NAME + "\"")
			.addOption("h", HELP_OPTION, false, "Print usage information");

		return gnuOptions;
	}

}
