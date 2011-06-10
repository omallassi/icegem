package com.googlecode.icegem.cacheutils.replication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;
import com.googlecode.icegem.utils.JavaProcessLauncher;

/**
 * Coordinates, configures and runs the set of guest nodes
 */
public class ReplicationProcessor {

	/* Set of locators */
	private Properties clustersProperties;

	/* Timeout, milliseconds */
	private long timeout;

	/* Name of the technical region */
	private final String regionName;

	private final boolean debugEnabled;
	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher(
		true, true, false);

	private final boolean quiet;

	private long processingStartedAt;

	/**
	 * Configures and creates the instance of replication processor
	 * 
	 * @param clustersProperties
	 *            - the set of locators
	 * @param timeout
	 *            - the timeout, milliseconds
	 * @param licenseFilePath
	 *            - the path to the license file
	 * @param licenseType
	 * @param regionName
	 *            - the name of technical region
	 * @param debugEnabled
	 * @param quiet
	 */
	public ReplicationProcessor(Properties clustersProperties, long timeout,
		String regionName, boolean debugEnabled, boolean quiet) {

		processingStartedAt = System.currentTimeMillis();

		this.clustersProperties = clustersProperties;
		this.timeout = timeout;
		this.regionName = regionName;
		this.debugEnabled = debugEnabled;
		this.quiet = quiet;
	}

	/**
	 * Creates, configures and runs the guest nodes and collects information
	 * form them
	 * 
	 * @return - 0 in case of success, 1 otherwise
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public int process() throws IOException, InterruptedException {
		debug("ReplicationProcessor#process(): Processing start");

		Properties gemfireProperties = PropertiesHelper.filterProperties(
			System.getProperties(), "gemfire.");
		String[] vmOptions = PropertiesHelper
			.propertiesToVMOptions(gemfireProperties);

		debug("ReplicationProcessor#process(): vmOptions = "
			+ Arrays.asList(vmOptions));

		List<Process> processesList = new ArrayList<Process>();
		for (Object keyObject : clustersProperties.keySet()) {
			String cluster = (String) keyObject;
			String clustersPropertiesString = PropertiesHelper
				.propertiesToString(clustersProperties);

			debug("ReplicationProcessor#process(): Starting GuestNode with parameters: cluster = "
				+ cluster
				+ ", clustersPropertiesString = "
				+ clustersPropertiesString
				+ ", timeout = "
				+ timeout
				+ ", regionName = " + regionName);

			Process process = javaProcessLauncher.runWithoutConfirmation(
				GuestNode.class,
				vmOptions,
				new String[] { cluster, clustersPropertiesString,
					String.valueOf(timeout), regionName,
					String.valueOf(debugEnabled), String.valueOf(quiet),
					String.valueOf(processingStartedAt) });

			debug("ReplicationProcessor#process(): Adding GuestNode to processList");

			processesList.add(process);
		}

		debug("ReplicationProcessor#process(): Waiting for processes finish");
		int mainExitCode = 0;
		int processNumber = 0;
		for (Process process : processesList) {
			debug("ReplicationProcessor#process(): Waiting for process #"
				+ processNumber);

			int exitCode = process.waitFor();

			if (exitCode != 0) {
				mainExitCode = 1;
			}
			debug("ReplicationProcessor#process(): Process #" + processNumber
				+ " finished with exitCode = " + exitCode);

			processNumber++;
		}

		debug("ReplicationProcessor#process(): Processing finished with mainExitCode = "
			+ mainExitCode);

		return mainExitCode;
	}

	private void debug(String message) {
		if (debugEnabled) {
			long currentTime = System.currentTimeMillis();
			long timeSinceProcessingStart = currentTime - processingStartedAt;
			System.err.println(timeSinceProcessingStart
				+ " [ReplicationProcessor] " + message);
		}
	}

}
