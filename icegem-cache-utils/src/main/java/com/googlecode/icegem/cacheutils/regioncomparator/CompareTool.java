package com.googlecode.icegem.cacheutils.regioncomparator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.cacheutils.Tool;
import com.googlecode.icegem.cacheutils.common.PeerCacheService;

public class CompareTool extends Tool {
	private static final Logger log = LoggerFactory
		.getLogger(CompareTool.class);
	private static String regionPathOption;
	private static String serversOption;
	private static List<String> scanPackagesOption;
	private static String locatorsOption;

	private static Map<String, String> createRegionPathNameMap(
		String regionOption) {
		Map<String, String> result = new TreeMap<String, String>();
		for (String regName : regionOption.split("/")) {
			String path = regionOption.substring(0,
				regionOption.lastIndexOf(regName))
				+ regName;
			result.put(path, regName);
		}
		return result;
	}

	protected void parseCommandLineArguments(String[] commandLineArguments) {
		Options options = constructGnuOptions();
		if (commandLineArguments.length < 1) {
			printHelp(options);
			System.exit(0);
		}
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine line = parser.parse(options, commandLineArguments);
			if (!line.hasOption("path") || line.hasOption("help")
				|| !(line.hasOption("locators") || line.hasOption("servers"))) {
				printHelp(options);
				System.exit(0);
			}

			if (line.hasOption("packages"))
				scanPackagesOption = Arrays.asList(line.getOptionValue(
					"packages").split(","));
			regionPathOption = line.getOptionValue("path");
			if (line.hasOption("locators"))
				locatorsOption = line.getOptionValue("locators");
			if (line.hasOption("servers"))
				serversOption = line.getOptionValue("servers");

		} catch (ParseException exp) {
			System.err.println("Parsing options failed. " + exp.getMessage());
			printHelp(options);
			System.exit(0);
		}
	}

	protected void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("compare [options]", options);
	}

	protected Options constructGnuOptions() {
		final Options gnuOptions = new Options();

		gnuOptions
			.addOption(
				"p",
				"path",
				true,
				"Region path to be compared. Only replicated region could be used. Example: /region1/region2")
			.addOption(
				"s",
				"servers",
				true,
				"Servers of GemFire system. For multi-cluster systems. Example: host1[port1],host2[port2]")
			.addOption(
				"l",
				"locators",
				true,
				"Locators of GemFire system. For intra-cluster checking. Example: host1[port1],host2[port2]")
			.addOption(
				"c",
				"packages",
				true,
				"Enumerate packages to scan for @AutoSerializable model classes. Delimiter is a comma sign.")
			.addOption("h", "help", false, "Print usage information");
		return gnuOptions;
	}

	private static void printResult(List resultObjs) {
		System.out.println("Missing                 Extra           Differnt");
		for (Object resultObj : resultObjs) {
			Object[][] result = (Object[][]) resultObj;
			Object[] missing = (result[0][0] == null) ? new Object[] {}
				: (Object[]) result[0][0];
			Object[] extra = (result[1][0] == null) ? new Object[] {}
				: (Object[]) result[1][0];
			Object[] different = (result[2][0] == null) ? new Object[] {}
				: (Object[]) result[2][0];
			System.out.println("  " + missing.length + "                      "
				+ extra.length + "                 " + different.length
				+ "      Node : " + result[3][0]);
			if (extra.length > 0) {
				System.out.println("---------------------");
				System.out.println("extra:");
				System.out.println(Arrays.toString(extra));
			}
			if (missing.length > 0) {
				System.out.println("---------------------");
				System.out.println("missing:");
				System.out.println(Arrays.toString(missing));
			}
			if (different.length > 0) {
				System.out.println("---------------------");
				System.out.println("different:");
				System.out.println(Arrays.toString(different));
			}
		}
	}

	public void execute(String[] args, boolean debugEnabled, boolean quiet) {
		parseCommandLineArguments(args);
		List compareResult = null;
		log.info("Connecting to system regions...");
		PeerCacheService peerCacheService = null;
		try {
			peerCacheService = new PeerCacheService(serversOption,
				scanPackagesOption);
		} catch (Exception e) {
			log.info("Failed to startup updater cache. " + e.getMessage());
			System.exit(0);
		}
		Map<String, String> regionPathNameMap = createRegionPathNameMap(regionPathOption);
		Region<?, ?> region = peerCacheService.createRegion(regionPathNameMap);
		if (locatorsOption == null) {
			RegionComparator comparator = new RegionComparator();
			log.info("Comparing regions...");
			compareResult = comparator.compareDifferentClusters(region);
			log.info("Closing client cache...");
			peerCacheService.close();
		} else {
			Object[] keySetOnServer = region.keySetOnServer().toArray();
			peerCacheService.close();
			RegionComparator comparator = new RegionComparator();
			log.info("Comparing regions...");
			compareResult = comparator.compareSingleCluster(keySetOnServer,
				locatorsOption, regionPathOption);
		}
		printResult(compareResult);
	}
}
