package com.googlecode.icegem.cacheutils.latencymeasurer;

import com.googlecode.icegem.cacheutils.Executable;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.cli.*;
import com.gemstone.gemfire.cache.*;
import static com.gemstone.gemfire.cache.DynamicRegionFactory.*;
import com.googlecode.icegem.cacheutils.latencymeasurer.listeners.LatencyDynamicRegionListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;


public class LatencyMeasurerManager implements Executable{

    private static final Logger log = LoggerFactory.getLogger(LatencyMeasurerManager.class);

    private static String clusterNameOption;
    private static String measureFrequencyOption;
    private static int measureFrequency;


    public static void main(String[] args) {
        parseCommandLineArguments(args);

        ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
        threadExecutor.execute(new Runnable() {

            public void run() {
                log.info("Creating utility region /latency_data and its subregion for storing latency data");
                DynamicRegionFactory dynRegFactory = get();
                dynRegFactory.registerDynamicRegionListener(new LatencyDynamicRegionListener(clusterNameOption, measureFrequency));
                dynRegFactory.open();

                Cache cache = new CacheFactory().create();
                Region latencyDataRegion = cache.getRegion("/latency_data");
                latencyDataRegion.put(cache.getDistributedSystem().getDistributedMember().getId(), clusterNameOption);
                Region clusterRegion = dynRegFactory.createDynamicRegion("/latency_data", clusterNameOption);
            }
        });

        Scanner in = new Scanner(System.in);
        System.out.print("For exit type any key: \n");
        in.nextLine();
        in.close();

        log.info("Exiting...");
        try {
            Cache cache = CacheFactory.getAnyInstance();
            DynamicRegionFactory dynRegFactory = get();
            dynRegFactory.destroyDynamicRegion("/latency_data/" + clusterNameOption);
            cache.close();
            threadExecutor.shutdown();
        } catch (Exception ex) {
             log.error("Exception was thrown.", ex);
        }
    }


    private static void parseCommandLineArguments(String[] commandLineArguments) {
        Options options = constructGnuOptions();
        if (commandLineArguments.length < 1) {
            printHelp(options);
            System.exit(0);
        }
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, commandLineArguments);
            if (line.hasOption("clustername")) {
                clusterNameOption = line.getOptionValue("clustername");
                if (clusterNameOption.indexOf("/") != -1) {
                    log.error("Cluster name should not contain /. Please try another name.");
                    System.exit(0);
                }
                if (line.hasOption("frequency")) {
                    measureFrequencyOption = line.getOptionValue("frequency");
                    try {
                        measureFrequency = Integer.parseInt(measureFrequencyOption);
                    } catch (Exception ex) {
                        log.error("Measure frequency should be integer.");
                        System.exit(0);
                    }
                }
            } else if (line.hasOption("help")) {
                printHelp(options);
                System.exit(0);
            } else {
                printHelp(options);
                System.exit(0);
            }
        }
        catch (ParseException exp) {
            log.error("Parsing options had failed.", exp);
            printHelp(options);
            System.exit(0);
        }
    }

    private static void printHelp(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("latency", options);
    }

    private static Options constructGnuOptions() {
        final Options gnuOptions = new Options();

        gnuOptions.addOption("n", "clustername", true, "The name of the local GemFire cluster. Should be unique")
                .addOption("f", "frequency", true, "Frequency of measurers in minutes")
                .addOption("h", "help", false, "Print usage information");
        return gnuOptions;
    }

    public void execute(String[] args)  {
        main(args);
    }
}
