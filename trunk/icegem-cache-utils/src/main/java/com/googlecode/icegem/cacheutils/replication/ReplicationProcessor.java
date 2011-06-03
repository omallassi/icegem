package com.googlecode.icegem.cacheutils.replication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.googlecode.icegem.utils.JavaProcessLauncher;

/**
 * Coordinates, configures and runs the set of guest nodes
 */
public class ReplicationProcessor {

	/* Set of locators */
	private Set<String> locatorsSet;

	/* Timeout, milliseconds */
	private long timeout;

	/* Path to the license file */
	private final String licenseFilePath;

	/* Name of the technical region */
	private final String regionName;

	/**
	 * Configures and creates the instance of replication processor
	 * 
	 * @param locatorsSet
	 *            - the set of locators
	 * @param timeout
	 *            - the timeout, milliseconds
	 * @param licenseFilePath
	 *            - the path to the license file
	 * @param regionName
	 *            - the name of technical region
	 */
	public ReplicationProcessor(Set<String> locatorsSet, long timeout,
		String licenseFilePath, String regionName) {
		this.locatorsSet = locatorsSet;
		this.timeout = timeout;
		this.licenseFilePath = licenseFilePath;
		this.regionName = regionName;
	}

	/**
	 * Create CSV list of remote locators excluding local locators from the set
	 * of all the locators
	 * 
	 * @param localLocators
	 *            - the local locators
	 * @return - the CSV list of remote locators
	 */
	private String prepareRemoteLocators(String localLocators) {
		Set<String> remoteLocatorsSet = new HashSet<String>(locatorsSet);
		remoteLocatorsSet.remove(localLocators);

		StringBuilder sb = new StringBuilder();

		Iterator<String> it = remoteLocatorsSet.iterator();
		while (it.hasNext()) {
			String locator = it.next();

			sb.append(locator.toString());

			if (it.hasNext()) {
				sb.append(",");
			}
		}

		return sb.toString();
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

		List<Process> processesList = new ArrayList<Process>();
		for (String locator : locatorsSet) {
			String localLocators = locator.toString();
			String remoteLocators = prepareRemoteLocators(locator);

			Process process = JavaProcessLauncher.runWithoutConfirmation(
				GuestNode.class, new String[] { localLocators, remoteLocators,
					String.valueOf(timeout), licenseFilePath, regionName });

			processesList.add(process);
		}

		int mainExitCode = 0;
		for (Process process : processesList) {
			int exitCode = process.waitFor();

			if (exitCode != 0) {
				mainExitCode = 1;
			}

			JavaProcessLauncher.printProcessOutput(process);
		}

		return mainExitCode;
	}

}
