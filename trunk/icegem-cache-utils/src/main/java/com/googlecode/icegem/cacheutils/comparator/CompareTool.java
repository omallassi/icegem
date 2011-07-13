package com.googlecode.icegem.cacheutils.comparator;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.googlecode.icegem.cacheutils.Tool;
import com.googlecode.icegem.cacheutils.common.Utils;
import com.googlecode.icegem.utils.JavaProcessLauncher;

public class CompareTool extends Tool {

	public static final String PARTITION = "partition";
	public static final String REPLICATE = "replicate";

	private static final String PACKAGES_OPTION = "packages";
	private static final String TARGET_SERVER_OPTION = "target-server";
	private static final String TARGET_REGION_OPTION = "target-region";
	private static final String TARGET_LOCATORS_OPTION = "target-locators";
	private static final String SOURCE_SERVER_OPTION = "source-server";
	private static final String SOURCE_REGION_OPTION = "source-region";
	private static final String SOURCE_LOCATORS_OPTION = "source-locators";
	private static final String LOAD_FACTOR_OPTION = "load-factor";
	private static final String HELP_OPTION = "help";

	private static final String SOURCE_FILENAME = "source-hashcode";
	private static final String TARGET_FILENAME = "target-hashcode";

	private static final int DEFAULT_LOAD_FACTOR = 50;

	private String sourceRegionName;
	private String sourceServer = null;
	private String sourceLocators = null;
	private String targetRegionName;
	private String targetServer = null;
	private String targetLocators = null;
	private List<String> packages;
	private int loadFactor = DEFAULT_LOAD_FACTOR;

	/* Field javaProcessLauncher */
	private JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher(
		true, true, false);

	public void execute(String[] args, boolean debugEnabled, boolean quiet) {
		try {
			parseCommandLineArguments(args);

			long sourceHashcode = -1;
			long targetHashcode = 1;

			Process sourceProcess;
			Process targetProcess;
			if (isPartitioned()) {

				sourceProcess = javaProcessLauncher.runWithoutConfirmation(
					HashcodeCalculator.class,
					null,
					new String[] { PARTITION, sourceLocators, sourceRegionName,
						SOURCE_FILENAME, String.valueOf(loadFactor),
						Utils.stringListToCsv(packages) });

				targetProcess = javaProcessLauncher.runWithoutConfirmation(
					HashcodeCalculator.class,
					null,
					new String[] { PARTITION, targetLocators, targetRegionName,
						TARGET_FILENAME, String.valueOf(loadFactor),
						Utils.stringListToCsv(packages) });

			} else {

				sourceProcess = javaProcessLauncher.runWithoutConfirmation(
					HashcodeCalculator.class,
					null,
					new String[] { REPLICATE, sourceServer, sourceRegionName,
						SOURCE_FILENAME, String.valueOf(loadFactor),
						Utils.stringListToCsv(packages) });

				targetProcess = javaProcessLauncher.runWithoutConfirmation(
					HashcodeCalculator.class,
					null,
					new String[] { REPLICATE, targetServer, targetRegionName,
						TARGET_FILENAME, String.valueOf(loadFactor),
						Utils.stringListToCsv(packages) });

			}

			sourceProcess.waitFor();
			targetProcess.waitFor();

			String sourceHashcodeString = FileService.read(SOURCE_FILENAME);
			String targetHashcodeString = FileService.read(TARGET_FILENAME);

			FileService.delete(SOURCE_FILENAME);
			FileService.delete(TARGET_FILENAME);

			sourceHashcode = Long.valueOf(sourceHashcodeString);
			targetHashcode = Long.valueOf(targetHashcodeString);

			if (sourceHashcode == targetHashcode) {
				System.out.println("equal");
				Utils.exitWithSuccess();
			} else {
				System.out.println("different");
				Utils.exitWithFailure();
			}
		} catch (Throwable t) {
			Utils.exitWithFailure("Unexpected throwable", t);
		}
	}

	private boolean isPartitioned() {
		return ((sourceLocators != null) && (targetLocators != null));
	}

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

			if (line.hasOption(SOURCE_REGION_OPTION)) {
				sourceRegionName = line.getOptionValue(SOURCE_REGION_OPTION);
			} else {
				printHelp(options);
			}

			if (line.hasOption(TARGET_REGION_OPTION)) {
				targetRegionName = line.getOptionValue(TARGET_REGION_OPTION);
			} else {
				printHelp(options);
			}

			if (line.hasOption(SOURCE_SERVER_OPTION)
				&& line.hasOption(TARGET_SERVER_OPTION)) {

				sourceServer = line.getOptionValue(SOURCE_SERVER_OPTION);
				targetServer = line.getOptionValue(TARGET_SERVER_OPTION);

			} else if (line.hasOption(SOURCE_LOCATORS_OPTION)
				&& line.hasOption(TARGET_LOCATORS_OPTION)) {

				sourceLocators = line.getOptionValue(SOURCE_LOCATORS_OPTION);
				targetLocators = line.getOptionValue(TARGET_LOCATORS_OPTION);

			} else {
				printHelp(options);
			}

			if (line.hasOption(PACKAGES_OPTION)) {
				packages = Arrays.asList(line.getOptionValue(PACKAGES_OPTION)
					.split(","));
			}

			if (line.hasOption(LOAD_FACTOR_OPTION)) {
				String loadFactorString = line
					.getOptionValue(LOAD_FACTOR_OPTION);

				try {
					loadFactor = Integer.parseInt(loadFactorString);
				} catch (Throwable t) {
					Utils.exitWithFailure("Cannot parse the "
						+ LOAD_FACTOR_OPTION + " option, value = "
						+ loadFactorString);
				}

				if ((loadFactor < 1) || (loadFactor > 100)) {
					Utils.exitWithFailure("The " + LOAD_FACTOR_OPTION
						+ " option, value = " + loadFactorString
						+ " is out of range [1, 100]");
				}
			}

		} catch (Throwable t) {
			Utils
				.exitWithFailure(
					"Throwable caught during the command-line arguments parsing",
					t);
		}
	}

	protected void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("compare <--" + SOURCE_REGION_OPTION + "> <--"
			+ TARGET_REGION_OPTION + "> < --" + SOURCE_SERVER_OPTION + " --"
			+ TARGET_SERVER_OPTION + " | --" + SOURCE_LOCATORS_OPTION + " --"
			+ TARGET_LOCATORS_OPTION + " > [--" + PACKAGES_OPTION + "]",
			options);

		Utils.exitWithFailure();
	}

	protected Options constructGnuOptions() {
		final Options gnuOptions = new Options();
		gnuOptions
			.addOption("sr", SOURCE_REGION_OPTION, true,
				"The name of source region")
			.addOption("ss", SOURCE_SERVER_OPTION, true,
				"Source server in format host[port]")
			.addOption("sl", SOURCE_LOCATORS_OPTION, true,
				"Source cluster locators in format host1[port1],host2[port2]")
			.addOption("tr", TARGET_REGION_OPTION, true,
				"The name of target region")
			.addOption("ts", TARGET_SERVER_OPTION, true,
				"Target server in format host[port]")
			.addOption("tl", TARGET_LOCATORS_OPTION, true,
				"Target cluster locators in format host1[port1],host2[port2]")
			.addOption(
				"lf",
				LOAD_FACTOR_OPTION,
				true,
				"The percent of time the comparator tries to use on each server. "
					+ "The possible values range [1, 100]. Default value is "
					+ DEFAULT_LOAD_FACTOR + ".")
			.addOption(
				"c",
				PACKAGES_OPTION,
				true,
				"Enumerate packages to scan for @AutoSerializable model classes. Delimiter is a comma sign.")
			.addOption("h", HELP_OPTION, false, "Print usage information");
		return gnuOptions;
	}

}
