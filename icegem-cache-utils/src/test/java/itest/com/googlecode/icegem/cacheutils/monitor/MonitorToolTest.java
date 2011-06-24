package itest.com.googlecode.icegem.cacheutils.monitor;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.googlecode.icegem.utils.PropertiesHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googlecode.icegem.cacheutils.Launcher;
import com.googlecode.icegem.cacheutils.monitor.MonitorTool;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;

public class MonitorToolTest {
	/** Field cacheServer1 */
	private static Process cacheServer1;
	/** Field cacheServer2 */
	private static Process cacheServer2;
	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher(
		false, false, false);

	private class CountingNodeEventHandler implements NodeEventHandler {

		private int count = 0;

		public void handle(NodeEvent event) {
			count++;
		}

		public int getCount() {
			return count;
		}
	}

	@BeforeClass
	public void setUp() throws IOException, InterruptedException,
		TimeoutException {
		startCacheServers();
	}

	@AfterClass
	public void tearDown() throws IOException, InterruptedException {
		stopCacheServers();
	}

	@Test
	public void testMainPositive() throws Exception {
		System.out.println("testMainPositive");

		PropertiesHelper propertiesHelper = new PropertiesHelper(
			"/monitorToolServerProperties40404.properties");

		MonitorTool tool = new MonitorTool();

		CountingNodeEventHandler handler = new CountingNodeEventHandler();
		tool.addNodeEventHandler(handler);

		tool.execute(
			new String[] { "-a", "-l",
				propertiesHelper.getStringProperty("locators") }, false, false);

		Thread.sleep(5 * 1000);

		assertThat(handler.getCount()).isEqualTo(4);

		tool.shutdown();
	}

	@Test
	public void testMainNegativeNoLocators() throws Exception {
		System.out.println("testMainNegativeNoLocators");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "monitor", "-a" });

		assertThat(exitCode).isEqualTo(1);
	}

	@Test
	public void testMainNegativeNoOptions() throws Exception {
		System.out.println("testMainNegativeNoOptions");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "monitor" });

		assertThat(exitCode).isEqualTo(1);
	}

	@Test
	public void testMainNegativeIncorrectLocator() throws Exception {
		System.out.println("testMainNegativeIncorrectLocator");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "monitor", "-a", "-l",
				"localhost[-1]" });

		assertThat(exitCode).isEqualTo(1);
	}

	@Test
	public void testMainNegativeLocatorIsNotSpecified() throws Exception {
		System.out.println("testMainNegativeLocatorIsNotSpecified");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher
			.runAndWaitProcessExitCode(Launcher.class, vmArguments,
				new String[] { "monitor", "-a", "-l" });

		assertThat(exitCode).isEqualTo(1);
	}

	@Test
	public void testIsServerAlivePositive() throws Exception {
		System.out.println("testIsServerAlivePositive");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "monitor", "-s",
				"localhost[40404]", "-t", "3000" });

		assertThat(exitCode).isEqualTo(0);
	}

	@Test
	public void testIsServerAliveNegativeDownServer() throws Exception {
		System.out.println("testIsServerAliveNegativeDownServer");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "monitor", "-s",
				"localhost[50505]", "-t", "3000" });

		assertThat(exitCode).isEqualTo(1);

	}

	@Test
	public void testIsServerAliveNegativeIncorrectServer() throws Exception {
		System.out.println("testIsServerAliveNegativeIncorrectServer");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "monitor", "-s",
				"localhost[-1]" });

		assertThat(exitCode).isEqualTo(1);
	}

	@Test
	public void testIsServerAliveNegativeNoServer() throws Exception {
		System.out.println("testIsServerAliveNegativeNoServer");

		String[] vmArguments = new String[] { "-Dgemfire.log-level=none" };

		int exitCode = javaProcessLauncher.runAndWaitProcessExitCode(
			Launcher.class, vmArguments, new String[] { "monitor", "-s" });

		assertThat(exitCode).isEqualTo(1);
	}

	private void startCacheServers() throws IOException, InterruptedException {
		cacheServer1 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=monitorToolServerProperties40404.properties" },
				null);
		cacheServer2 = javaProcessLauncher
			.runWithConfirmation(
				ServerTemplate.class,
				new String[] { "-DgemfirePropertyFile=monitorToolServerProperties40405.properties" },
				null);
	}

	private void stopCacheServers() throws IOException, InterruptedException {
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
	}

}
