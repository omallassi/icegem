package com.googlecode.icegem.cacheutils;

import com.googlecode.icegem.cacheutils.latencymeasurer.LatencyMeasurerManager;
import com.googlecode.icegem.cacheutils.monitor.MonitoringTool;
import com.googlecode.icegem.cacheutils.regioncomparator.ComparatorManager;
import com.googlecode.icegem.cacheutils.signallistener.SignalWaiter;
import com.googlecode.icegem.cacheutils.updater.UpdateManager;

import java.util.HashMap;
import java.util.Map;

public class Launcher {

	private enum Command {
		COMPARATOR("comparator", new ComparatorManager()),
        LATENCY_MEASURE("latency-measure", new LatencyMeasurerManager()),
        MONITOR("monitor", new MonitoringTool()),
        UPDATER("updater", new UpdateManager()),
        WAITER("signal", new SignalWaiter());

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
            for (Command command: values())
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
        Executable exec = Command.getUtil(commandName);
        if (exec != null)
            exec.run(commandArgs);
		else {
            System.err.println("command not found: " + commandName);
			printHelp();
        }

	}
}
