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

public class ReplicationManager implements Executable {

	private static final String TIMEOUT_OPTION = "timeout";
	private static final String LOCATORS_OPTION = "locators";
	private static final String REGION_OPTION = "region";
	private static final String LICENSE_FILE_OPTION = "license-file";
	private static final String HELP_OPTION = "help";

	private static final long DEFAULT_TIMEOUT = 60 * 1000;
	private static final String DEFAULT_LICENSE_FILE_PATH = "gemfireLicense.zip";
	private static final String DEFAULT_REGION_NAME = "proxy";

	private static Set<String> locatorsSet;
	private static long timeout = DEFAULT_TIMEOUT;
	private static String licenseFilePath = DEFAULT_LICENSE_FILE_PATH;
	private static String regionName = DEFAULT_REGION_NAME;

	public void run(String[] args) {
		try {
			main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	private static Set<String> parseLocators(String csvLocators)
		throws PatternSyntaxException, NumberFormatException {

		String[] locators = csvLocators.split(",");
		List<String> locatorsList = Arrays.asList(locators);

		return new HashSet<String>(locatorsList);
	}

	private static void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("replication", options);

		System.exit(-1);
	}

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
