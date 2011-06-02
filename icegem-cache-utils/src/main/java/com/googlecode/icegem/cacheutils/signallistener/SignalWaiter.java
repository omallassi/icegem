package com.googlecode.icegem.cacheutils.signallistener;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.cacheutils.Executable;
import org.apache.commons.cli.*;

import java.util.concurrent.TimeUnit;

/**
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class SignalWaiter implements Executable {

    /**
     * @param regionToListen region that will contain key
     * @param key key for listening
     * @param timeout how long util waits for key's appearance
     * @param checkInterval how often util checks region
     * @return 0 if key was founded, otherwise 1
     * */
    public static int waitSignal(Region regionToListen, Object key, long timeout, long checkInterval) throws InterruptedException{
        if (regionToListen == null)
            throw new NullPointerException("region is null");
        if (checkInterval > timeout)
            throw new IllegalArgumentException("check interval (" + checkInterval + ") is longer then timeout(" + timeout + ")");

        long fromTime = System.currentTimeMillis();
        //todo: diff time (sec, millisec, etc)
        while((System.currentTimeMillis() - fromTime) <= timeout) {
            if (regionToListen.containsKeyOnServer(key)) {
                return 0;
            }
            TimeUnit.MILLISECONDS.sleep(checkInterval);
        }
        return 1;
    }

    public static void main(String[] args)  {
        CommandLineParser cmdParser = new GnuParser();
        Options options = getAvailableOptions();
        CommandLine cmd  = null;
        try {
            cmd = cmdParser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException("error parsing cmd args", e);
        }
        if(!requiredOptionsExist(cmd))
            return;

        ClientCacheFactory clientCacheFactory = new ClientCacheFactory();
        String locators = cmd.getOptionValue("locators");
        for(String locator: locators.split(",")) {
            String host = locator.trim().substring(0, locator.indexOf("["));
            int port = Integer.parseInt(locator.substring(locator.indexOf("[") + 1, locator.indexOf("]")));
            clientCacheFactory.addPoolLocator(host, port);
        }

        ClientCache client;
        client = clientCacheFactory.create();

        String regionNameToListen = cmd.getOptionValue("region");
        Region signalRegion = client.createClientRegionFactory(ClientRegionShortcut.PROXY)
                                    .create(regionNameToListen);
        int timeout = 60000;
        if (cmd.hasOption("timeout"))
            timeout = Integer.parseInt(cmd.getOptionValue("timeout"));
        else
            System.out.println("using default value for timeout: " + timeout);

        int checkInterval = 1000;
        if (cmd.hasOption("checkInterval"))
            checkInterval = Integer.parseInt(cmd.getOptionValue("checkInterval"));
        else
            System.out.println("using default value for check interval " + checkInterval);

        String keyToListen = cmd.getOptionValue("key");
        //example
        int result  = 0;
        try {
            result = waitSignal(signalRegion, keyToListen, timeout, checkInterval);
        } catch (InterruptedException e) {
            throw new RuntimeException("error waiting key", e);
        }
        System.out.println("status is " + result);

        client.close();
    }

    private static boolean requiredOptionsExist(CommandLine cmd) {
        if (!cmd.hasOption("region") || !cmd.hasOption("locators") || !cmd.hasOption("key")) {
            System.err.println("some of required options is absent: \'region\', \'locators\', \'key\'");
            return false;
        }
        return true;
    }

    private static Options getAvailableOptions() {
        Options options = new Options();
        options.addOption("region", true, "region where key appearance is checking");
        options.addOption("locators", true, "available locators");
        options.addOption("key", true, "key for checking");
        options.addOption("timeout", true, "check duration");
        options.addOption("checkInterval", true, "key's check interval");
        return options;
    }

    public void run(String[] args)  {
        main(args);
    }
}
