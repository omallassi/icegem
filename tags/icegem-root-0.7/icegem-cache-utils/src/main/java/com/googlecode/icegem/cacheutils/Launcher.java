package com.googlecode.icegem.cacheutils;

import com.googlecode.icegem.cacheutils.latencymeasurer.LatencyMeasurerManager;
import com.googlecode.icegem.cacheutils.monitor.MonitoringTool;
import com.googlecode.icegem.cacheutils.regioncomparator.ComparatorManager;
import com.googlecode.icegem.cacheutils.updater.UpdateManager;

public class Launcher {

	private enum Command {
		COMPARATOR("comparator"), LATENCY_MEASURE("latency-measure"), MONITOR(
			"monitor"), UPDATER("updater");

		private String name;

		private Command(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Command get(String name) {
			if ((name != null) && (name.trim().length() > 0)) {
				for (Command command : values()) {
					if (name.equals(command.getName())) {
						return command;
					}
				}
			}

			return null;
		}

	}

	private static void printHelp() {

		StringBuilder sb = new StringBuilder();

		sb.append("Usage: java -jar icegem-cache-utils-<version>.jar <");

		Command[] commands = Command.values();
		for (int i = 0; i < commands.length; i++) {
			sb.append(commands[i].getName());
			if (i < (commands.length - 1)) {
				sb.append(" | ");
			}
		}

		sb.append("> [command_specific_options]");

		System.out.println(sb.toString());

	}

	private static String[] removeCommandFromArgs(String[] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException(
				"Args cannot have the length less than 1");
		}

		String[] result = new String[args.length - 1];

		for (int i = 1; i < args.length; i++) {
			result[i - 1] = args[i];
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			printHelp();
			return;
		}

		String commandName = args[0];

		String[] commandArgs = removeCommandFromArgs(args);

		Command command = Command.get(commandName);

		if (Command.COMPARATOR.equals(command)) {
			ComparatorManager.main(commandArgs);
		} else if (Command.LATENCY_MEASURE.equals(command)) {
			LatencyMeasurerManager.main(commandArgs);
		} else if (Command.MONITOR.equals(command)) {
			MonitoringTool.main(commandArgs);
		} else if (Command.UPDATER.equals(command)) {
			UpdateManager.main(commandArgs);
		} else {
			printHelp();
			return;
		}

	}
}
