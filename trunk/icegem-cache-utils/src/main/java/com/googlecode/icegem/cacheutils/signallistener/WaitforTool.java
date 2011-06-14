package com.googlecode.icegem.cacheutils.signallistener;

import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.cacheutils.Tool;
import com.googlecode.icegem.cacheutils.common.Utils;

/**
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class WaitforTool extends Tool {

	private static final int DEFAULT_CHECK_INTERVAL = 1000;
	private static final int DEFAULT_TIMEOUT = 60000;

	private String locators;
	private String regionNameToListen;
	private int timeout;
	private int checkInterval;
	private String keyToListen;

	/**
	 * @param regionToListen
	 *            region that will contain key
	 * @param key
	 *            key for listening
	 * @param timeout
	 *            how long util waits for key's appearance
	 * @param checkInterval
	 *            how often util checks region
	 * @return 0 if key was founded, otherwise 1
	 * */
	public static int waitSignal(Region regionToListen, Object key,
		long timeout, long checkInterval) throws InterruptedException {
		if (regionToListen == null)
			throw new NullPointerException("region is null");
		if (checkInterval > timeout)
			throw new IllegalArgumentException("check interval ("
				+ checkInterval + ") is longer then timeout(" + timeout + ")");

		long fromTime = System.currentTimeMillis();
		// todo: diff time (sec, millisec, etc)
		while ((System.currentTimeMillis() - fromTime) <= timeout) {
			if (regionToListen.containsKeyOnServer(key)) {
				return 0;
			}
			TimeUnit.MILLISECONDS.sleep(checkInterval);
		}
		return 1;
	}

	protected Options constructGnuOptions() {
		Options options = new Options();
		options.addOption("region", true,
			"region where key appearance is checking");
		options.addOption("locators", true, "available locators");
		options.addOption("key", true, "key for checking");
		options.addOption("timeout", true, "check duration");
		options.addOption("checkInterval", true, "key's check interval");
		return options;
	}

	@Override
	protected void parseCommandLineArguments(String[] commandLineArguments) {
		CommandLineParser cmdParser = new GnuParser();
		Options options = constructGnuOptions();

		CommandLine cmd = null;
		try {
			cmd = cmdParser.parse(options, commandLineArguments);
		} catch (ParseException e) {
			throw new RuntimeException("error parsing cmd args", e);
		}

		if (!cmd.hasOption("region") || !cmd.hasOption("locators")
			|| !cmd.hasOption("key")) {
			printHelp(options);
            Utils.exitWithFailure();
		}

		locators = cmd.getOptionValue("locators");

		regionNameToListen = cmd.getOptionValue("region");

		timeout = DEFAULT_TIMEOUT;
		if (cmd.hasOption("timeout"))
			timeout = Integer.parseInt(cmd.getOptionValue("timeout"));
		else
			System.out.println("using default value for timeout: " + timeout);

		checkInterval = DEFAULT_CHECK_INTERVAL;
		if (cmd.hasOption("checkInterval"))
			checkInterval = Integer.parseInt(cmd
				.getOptionValue("checkInterval"));
		else
			System.out.println("using default value for check interval "
				+ checkInterval);

		keyToListen = cmd.getOptionValue("key");
	}

	protected void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("waitfor [options]", options);
	}

	public void execute(String[] args, boolean debugEnabled, boolean quiet) {
		parseCommandLineArguments(args);
		
		ClientCacheFactory clientCacheFactory = new ClientCacheFactory();
		for (String locator : locators.split(",")) {
			String host = locator.trim().substring(0, locator.indexOf("["));
			int port = Integer.parseInt(locator.substring(
				locator.indexOf("[") + 1, locator.indexOf("]")));
			clientCacheFactory.addPoolLocator(host, port);
		}

		ClientCache client;
		client = clientCacheFactory.create();

		Region signalRegion = client.createClientRegionFactory(
			ClientRegionShortcut.PROXY).create(regionNameToListen);
		// example
		int result = 0;
		try {
			result = waitSignal(signalRegion, keyToListen, timeout,
				checkInterval);
		} catch (InterruptedException e) {
			throw new RuntimeException("error waiting key", e);
		}
		System.out.println("status is " + result);

		client.close();
	}
}
