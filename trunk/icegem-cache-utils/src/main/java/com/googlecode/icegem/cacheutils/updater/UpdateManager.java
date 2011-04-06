package com.googlecode.icegem.cacheutils.updater;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemstone.gemfire.admin.AdminException;
import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.cacheutils.common.AdminService;
import com.googlecode.icegem.cacheutils.common.PeerCacheService;

public class UpdateManager {
	private static final Logger log = LoggerFactory.getLogger(UpdateManager.class);
	private static boolean withSubRegionsOption;
	private static String regionsOption;
	private static String locatorOption;
    private static String serverOption;
    private static List<String> scanPackagesOption;
	
	public static void main(String[] commandLineArguments) {
		parseCommandLineArguments(commandLineArguments);
		log.info("Connecting to the system as admin member...");
		AdminService admin = null;
		try {
			admin = new AdminService(locatorOption);
		} catch (Exception e) {
			log.info("Failed to connect to the system. " + e.getMessage());
			System.exit(0);
		}
		log.info("Collect system regions...");
		Map<String, String> regionNames = null;
		try {
			regionNames = admin.getRegionNames(regionsOption, withSubRegionsOption);
		} catch (AdminException e) {
			log.info("Failed to get system regions. " + e.getMessage());
            admin.close();
			System.exit(0);
		}
        log.info("Found following system regions: " + regionNames.values());
        log.info("Closing admin member...");
        admin.close();
        

		log.info("Connecting to system regions...");
		PeerCacheService peerCacheService = null;
        try {
            peerCacheService = new PeerCacheService(serverOption, scanPackagesOption);
        } catch (Exception e) {
            log.info("Failed to startup updater cache. " + e.getMessage());
			System.exit(0);
        }
        Set<Region<?,?>> regions = peerCacheService.createRegions(regionNames);
		Updater updater = new Updater();
		log.info("Updating regions...");
		updater.updateRegions(regions);
        log.info("Regions update finished successfuly");
        log.info("Closing client cache...");
		peerCacheService.close();
	}

	private static void parseCommandLineArguments(String[] commandLineArguments) {
		Options options = constructGnuOptions();
		if (commandLineArguments.length < 1) {
			printHelp(options);
            System.exit(0);
		}
		CommandLineParser parser = new GnuParser();
	    try {
	        CommandLine line = parser.parse( options, commandLineArguments );
	        if(line.hasOption("regions")) {
	        	withSubRegionsOption = line.hasOption("subregions");
	        	regionsOption = line.getOptionValue("regions");
	        	locatorOption = line.getOptionValue("locator");
                serverOption = line.getOptionValue("server");
                if(line.hasOption("packages"))
                    scanPackagesOption = Arrays.asList(line.getOptionValue("packages").split(","));
	        } else if (line.hasOption("all")) {
	        	regionsOption = "all";
	        	locatorOption = line.getOptionValue("locator");
                serverOption = line.getOptionValue("server");
                if(line.hasOption("packages"))
                    scanPackagesOption = Arrays.asList(line.getOptionValue("packages").split(","));
	        } else if (line.hasOption("help")) {
	        	printHelp(options);
	        	System.exit(0);
	        } else {
	        	printHelp(options);
	        	System.exit(0);
	        }
	    }
	    catch(ParseException exp) {
	        System.err.println( "Parsing options failed. " + exp.getMessage() );
	        printHelp(options);
	        System.exit(0);
	    }
	}
	
	private static void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "updater", options );
	}
	
	private static Options constructGnuOptions() {
		final Options gnuOptions = new Options();

		gnuOptions.addOption("r", "regions", true, "Enumerate regions to be updated here. Delimiter is a comma sign. Example: region1,region2,region3...")
				.addOption("c", "subregions", false, "Indicate whether to update all subregions of mentioned regions")
				.addOption("a", "all", false, "Update all regions in system")
				.addOption("l", "locator", true, "Locator of GemFire system. Example: host[port]")
                .addOption("s", "server", true, "Server of GemFire system. Example: host[port]")
                .addOption("p", "packages", true, "Enumerate packages to scan for @AutoSerializable model classes. Delimiter is a comma sign.")
				.addOption("h", "help", false, "Print usage information");
		return gnuOptions;
	}
}
