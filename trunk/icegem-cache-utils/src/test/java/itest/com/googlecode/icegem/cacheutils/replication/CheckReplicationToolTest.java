package itest.com.googlecode.icegem.cacheutils.replication;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googlecode.icegem.cacheutils.Launcher;
import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 * Starts three locators, three gateways. Each gateway is connected to separate
 * locator. Start replication measurement tool and expects that it will return 0
 * as exit code. This means that replication between all the clusters works.
 */
public class CheckReplicationToolTest {
	/** Field gatewayA */
	private static Process gatewayA;
	/** Field gatewayB */
	private static Process gatewayB;
	/** Field gatewayC */
	private static Process gatewayC;

	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

	@BeforeClass
	public void setUp() throws IOException, InterruptedException,
		TimeoutException {
		startGateways();
	}

	@AfterClass
	public void tearDown() throws IOException, InterruptedException {
		stopGateways();
	}

	@Test
	public void testMainPositive() throws Exception {
		System.out.println("testMainPositive");

		PropertiesHelper propertiesHelper = new PropertiesHelper(
			"/checkReplicationToolGatewayA.properties");

		Process process = JavaProcessLauncher.runWithoutConfirmation(
			Launcher.class,
			new String[] { "check-replication", "-c",
				"clusterA=localhost[18081]", "-c", "clusterB=localhost[18082]",
				"-c", "clusterC=localhost[18083]", "-lf",
				propertiesHelper.getStringProperty("license-file"), "-lt",
				propertiesHelper.getStringProperty("license-type"), "-t",
				"30000" });

		JavaProcessLauncher.printProcessOutput(process);
		JavaProcessLauncher.printProcessError(process);

		int exitCode = process.waitFor();

		assertThat(exitCode).isEqualTo(0);

	}

	public void testMainPositiveWithWrongLocators() throws Exception {
		System.out.println("testMainPositiveWithWrongLocators");

		PropertiesHelper propertiesHelper = new PropertiesHelper(
			"/checkReplicationToolGatewayA.properties");

		Process process = JavaProcessLauncher.runWithoutConfirmation(
			Launcher.class, new String[] { "check-replication", "-c",
				"clusterA=localhost[18081],localhost[18084]",
				"-c", "clusterB=localhost[18082],localhost[18086]", "-c",
				"clusterC=localhost[18083],localhost[18087]", "-lf",
				propertiesHelper.getStringProperty("license-file"), "-lt",
				propertiesHelper.getStringProperty("license-type"), "-t",
				"30000" });

		JavaProcessLauncher.printProcessOutput(process);
		JavaProcessLauncher.printProcessError(process);

		int exitCode = process.waitFor();

		assertThat(exitCode).isEqualTo(0);

	}

	@Test
	public void testMainNegativeWrongLocator() throws Exception {
		System.out.println("testMainNegativeWrongLocator");

		PropertiesHelper propertiesHelper = new PropertiesHelper(
			"/checkReplicationToolGatewayA.properties");

		Process process = JavaProcessLauncher.runWithoutConfirmation(
			Launcher.class,
			new String[] { "check-replication", "-c",
				"clusterA=localhost[18081]", "-c", "clusterB=localhost[18082]",
				"-c", "clusterD=localhost[18084]", "-lf",
				propertiesHelper.getStringProperty("license-file"), "-lt",
				propertiesHelper.getStringProperty("license-type"), "-t",
				"10000" });

		JavaProcessLauncher.printProcessOutput(process);
		JavaProcessLauncher.printProcessError(process);

		int exitCode = process.waitFor();

		assertThat(exitCode).isEqualTo(1);

	}

	@Test
	public void testMainNegativeDefaultLicense() throws Exception {
		System.out.println("testMainNegativeDefaultLicense");

		Process process = JavaProcessLauncher.runWithoutConfirmation(
			Launcher.class, new String[] { "check-replication", "-c",
				"clusterA=localhost[18081]", "-c", "clusterB=localhost[18082]",
				"-c", "clusterC=localhost[18083]", "-t", "10000" });

		JavaProcessLauncher.printProcessOutput(process);
		JavaProcessLauncher.printProcessError(process);

		int exitCode = process.waitFor();

		assertThat(exitCode).isEqualTo(1);

	}

	@Test
	public void testMainNegativeEmptyParameters() throws Exception {
		System.out.println("testMainNegativeEmptyParameters");

		Process process = JavaProcessLauncher.runWithoutConfirmation(
			Launcher.class, new String[] { "check-replication" });

		JavaProcessLauncher.printProcessOutput(process);
		JavaProcessLauncher.printProcessError(process);

		int exitCode = process.waitFor();

		assertThat(exitCode).isEqualTo(1);

	}

	@Test
	public void testMainNegativeIncorrectRegion() throws Exception {
		System.out.println("testMainNegativeIncorrectRegion");

		PropertiesHelper propertiesHelper = new PropertiesHelper(
			"/checkReplicationToolGatewayA.properties");

		Process process = JavaProcessLauncher.runWithoutConfirmation(
			Launcher.class,
			new String[] { "check-replication", "-c",
				"clusterA=localhost[18081]", "-c", "clusterB=localhost[18082]",
				"-c", "clusterC=localhost[18083]", "-lf",
				propertiesHelper.getStringProperty("license-file"), "-lt",
				propertiesHelper.getStringProperty("license-type"), "-t",
				"10000", "-r", "wrong" });

		JavaProcessLauncher.printProcessOutput(process);
		JavaProcessLauncher.printProcessError(process);

		int exitCode = process.waitFor();

		assertThat(exitCode).isEqualTo(1);

	}

	private void startGateways() throws IOException, InterruptedException {
		gatewayA = javaProcessLauncher.runServerWithConfirmation(
			ServerTemplate.class, "checkReplicationToolGatewayA.properties");
		gatewayB = javaProcessLauncher.runServerWithConfirmation(
			ServerTemplate.class, "checkReplicationToolGatewayB.properties");
		gatewayC = javaProcessLauncher.runServerWithConfirmation(
			ServerTemplate.class, "checkReplicationToolGatewayC.properties");
	}

	private void stopGateways() throws IOException, InterruptedException {
		javaProcessLauncher.stopBySendingNewLineIntoProcess(gatewayA);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(gatewayB);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(gatewayC);
	}

}
