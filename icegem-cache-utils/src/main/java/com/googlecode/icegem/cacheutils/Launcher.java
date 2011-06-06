package com.googlecode.icegem.cacheutils;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.icegem.cacheutils.monitor.MonitorTool;
import com.googlecode.icegem.cacheutils.regioncomparator.CompareTool;
import com.googlecode.icegem.cacheutils.replication.CheckReplicationTool;
import com.googlecode.icegem.cacheutils.signallistener.WaitforTool;
import com.googlecode.icegem.cacheutils.updater.UpdateTool;

public class Launcher {

	private enum Command {
		COMPARE("compare", new CompareTool()), MONITOR("monitor",
			new MonitorTool()), CHECK_REPLICATION("check-replication",
			new CheckReplicationTool()), UPDATE("update", new UpdateTool()), WAITFOR(
			"waitfor", new WaitforTool());

		private String name;
		private Executable exec;

		private Command(String name, Executable exec) {
			this.name = name;
			this.exec = exec;

		}

		public String getName() {
			return name;
		}

		public Executable getExec() {
			return exec;
		}

		private static Map<String, Command> map = new HashMap<String, Command>();
		static {
			for (Command command : values())
				map.put(command.getName(), command);
		}

		public static Command get(String name) {
			return map.get(name);

		}

		public static boolean hasName(String name) {
			return map.containsKey(name);
		}

		public static Executable getUtil(String name) {
			if (hasName(name))
				return get(name).getExec();
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
		Executable tool = Command.getUtil(commandName);
		if (tool != null) {
			tool.execute(commandArgs);
		} else {
			System.err.println("command not found: " + commandName);
			printHelp();
		}

	}
}
