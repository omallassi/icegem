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

import com.googlecode.icegem.cacheutils.Executable;

/**
 * The main class of the replication measurement tool. It parses command-line
 * options, configures and creates the guest nodes, and collects their
 * responses.
 * 
 * Responses with exit code 0 in case of all the relations established, 1
 * otherwise.
 */
public class ReplicationManager implements Executable {

	/* Name of timeout option */
	private static final String TIMEOUT_OPTION = "timeout";

	/* Name of locators option */
	private static final String LOCATORS_OPTION = "locators";

	/* Name of region option */
	private static final String REGION_OPTION = "region";

	/* Name of license file option */
	private static final String LICENSE_FILE_OPTION = "license-file";

	/* Name of help option */
	private static final String HELP_OPTION = "help";

	/* Default timeout is 1 minute */
	private static final long DEFAULT_TIMEOUT = 60 * 1000;

	/* Default license file is gemfireLicense.zip */
	private static final String DEFAULT_LICENSE_FILE_PATH = "gemfireLicense.zip";

	/* Default region name is "proxy" */
	private static final String DEFAULT_REGION_NAME = "proxy";

	/* List of clusters' locators to check */
	private static Set<String> locatorsSet;

	/* Waiting timeout */
	private static long timeout = DEFAULT_TIMEOUT;

	/* Path to the license file */
	private static String licenseFilePath = DEFAULT_LICENSE_FILE_PATH;

	/* Technical region name */
	private static String regionName = DEFAULT_REGION_NAME;

	/**
	 * Runs the tool. All the tools run in this way.
	 */
	public void run(String[] args) {
		try {
			main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Entry point of the tool
	 * 
	 * @param args
	 *            - the list of command-line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
			parseCommandLineArguments(args);

			ReplicationProcessor processor = new ReplicationProcessor(
				locatorsSet, timeout, licenseFilePath, regionName);

			int mainExitCode = processor.process();

			System.exit(mainExitCode);
		} catch (Throwable t) {
			System.err.println(t.getMessage());
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
	private static void parseCommandLineArguments(String[] commandLineArguments) {
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
			System.err
				.println("Parsing of options failed. Please check that you use correct option or specify a server in format host[port].");
			printHelp(options);
		}
	}

	/**
	 * Parses CSV locators string and return set of locators 
	 * 
	 * @param csvLocators - the CSV locators string 
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
	 * @param options - the GNU options
	 */
	private static void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("replication", options);

		System.exit(-1);
	}

	/**
	 * Constructs the set of GNU options
	 *  
	 * @return - the constructed options
	 */
	private static Options constructGnuOptions() {
		final Options gnuOptions = new Options();

		gnuOptions
			.addOption("l", LOCATORS_OPTION, true,
				"List of clusters' locators to check. Example: host1[port1],host2[port2]")
			.addOption("t", TIMEOUT_OPTION, true, "Timeout, seconds")
			.addOption("lf", LICENSE_FILE_OPTION, true,
				"The path to non default license file")
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
