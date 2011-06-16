package itest.com.googlecode.icegem.cacheutils.monitor;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.icegem.cacheutils.monitor.MonitorTool;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;

public class MonitorToolTest {
	/** Field cacheServer1  */
    private static Process cacheServer1;
	/** Field cacheServer2 */
	private static Process cacheServer2;
	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

	private class CountingNodeEventHandler implements NodeEventHandler {

		private int count = 0;

		public void handle(NodeEvent event) {
			count++;
		}

		public int getCount() {
			return count;
		}
	}

	@BeforeMethod
	public void setUp() throws IOException, InterruptedException,
		TimeoutException {
		startCacheServers();
	}

	@AfterMethod
	public void tearDown() throws IOException, InterruptedException {
		stopCacheServers();
	}

	@Test
	public void testMain() throws Exception {
		MonitorTool tool = new MonitorTool();
        tool.init();

		CountingNodeEventHandler handler = new CountingNodeEventHandler();
		tool.addNodeEventHandler(handler);

		tool.start();

		Thread.sleep(5 * 1000);

		assertThat(handler.getCount()).isEqualTo(4);
		
		tool.shutdown();
	}

	@Test
	public void testIsServerAlivePositive() throws Exception {
		boolean serverAlive = MonitorTool.isServerAlive("localhost", 40404);
		assertThat(serverAlive).isTrue();
	}

	@Test
	public void testIsServerAliveNegative() throws Exception {
		boolean serverAlive = MonitorTool.isServerAlive("localhost", 50505);
		assertThat(serverAlive).isFalse();
	}

	private void startCacheServers() throws IOException, InterruptedException {
		cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=monitorToolServerProperties40404.properties"}, null);
		cacheServer2 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=monitorToolServerProperties40405.properties"}, null);
	}

	private void stopCacheServers() throws IOException, InterruptedException {
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
	}

}
