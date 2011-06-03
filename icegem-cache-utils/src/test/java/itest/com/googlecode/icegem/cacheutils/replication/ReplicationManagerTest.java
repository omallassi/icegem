package itest.com.googlecode.icegem.cacheutils.replication;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.icegem.cacheutils.monitor.utils.PropertiesHelper;
import com.googlecode.icegem.cacheutils.replication.ReplicationManager;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 * Starts three locators, three gateways. Each gateway is connected to separate
 * locator. Start replication measurement tool and expects that it will return 0
 * as exit code. This means that replication between all the clusters works.
 */
public class ReplicationManagerTest {
	/** Field gatewayA */
	private static Process gatewayA;
	/** Field gatewayB */
	private static Process gatewayB;
	/** Field gatewayC */
	private static Process gatewayC;

	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

	@BeforeMethod
	public void setUp() throws IOException, InterruptedException,
		TimeoutException {
		startGateways();
	}

	@AfterMethod
	public void tearDown() throws IOException, InterruptedException {
		stopGateways();
	}

	@Test
	public void testMain() throws Exception {

		PropertiesHelper propertiesHelper = new PropertiesHelper(
			"/replicationManagerGatewayA.properties");

		Process process = JavaProcessLauncher.runWithoutConfirmation(
			ReplicationManager.class, new String[] { "-l",
				"localhost[18081],localhost[18082],localhost[18083]", "-lf",
				propertiesHelper.getStringProperty("license-file"), "-t",
				"30000" });

		int exitCode = process.waitFor();

		assertThat(exitCode).isEqualTo(0);

	}

	private void startGateways() throws IOException, InterruptedException {
		gatewayA = javaProcessLauncher.runServerWithConfirmation(
			ServerTemplate.class, "replicationManagerGatewayA.properties");
		gatewayB = javaProcessLauncher.runServerWithConfirmation(
			ServerTemplate.class, "replicationManagerGatewayB.properties");
		gatewayC = javaProcessLauncher.runServerWithConfirmation(
			ServerTemplate.class, "replicationManagerGatewayC.properties");
	}

	private void stopGateways() throws IOException, InterruptedException {
		javaProcessLauncher.stopBySendingNewLineIntoProcess(gatewayA);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(gatewayB);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(gatewayC);
	}

}
