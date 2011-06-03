package com.googlecode.icegem.cacheutils.replication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.googlecode.icegem.utils.JavaProcessLauncher;

public class ReplicationProcessor {

	private Set<String> locatorsSet;
	private long timeout;
	private final String licenseFilePath;
	private final String regionName;

	public ReplicationProcessor(Set<String> locatorsSet, long timeout,
		String licenseFilePath, String regionName) {
		this.locatorsSet = locatorsSet;
		this.timeout = timeout;
		this.licenseFilePath = licenseFilePath;
		this.regionName = regionName;
	}

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
