package com.googlecode.icegem.cacheutils.replication;

import java.io.IOException;
import java.util.ArrayList;
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

	/* Path to the license file */
	private final String licenseFilePath;

	/* Name of the technical region */
	private final String regionName;

	private final String licenseType;

	private final boolean debugEnabled;
	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher(
		true, true);

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
	 */
	public ReplicationProcessor(Properties clustersProperties, long timeout,
		String licenseFilePath, String licenseType, String regionName,
		boolean debugEnabled) {
		this.clustersProperties = clustersProperties;
		this.timeout = timeout;
		this.licenseFilePath = licenseFilePath;
		this.licenseType = licenseType;
		this.regionName = regionName;
		this.debugEnabled = debugEnabled;
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

		long processingStartedAt = System.currentTimeMillis();

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
				+ ", licenseFilePath = "
				+ licenseFilePath
				+ ", licenseType = "
				+ licenseType + ", regionName = " + regionName);

			Process process = javaProcessLauncher.runWithoutConfirmation(
				GuestNode.class,
				null,
				new String[] { cluster, clustersPropertiesString,
					String.valueOf(timeout), licenseFilePath, licenseType,
					regionName, String.valueOf(debugEnabled),
					String.valueOf(processingStartedAt) });

			debug("ReplicationProcessor#process(): Adding GuestNode to processList");

			processesList.add(process);
		}

		debug("ReplicationProcessor#process(): Adding JavaProcessLauncher#printProcessError(Process) to each process");

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
			System.err.println(message);
		}
	}
}