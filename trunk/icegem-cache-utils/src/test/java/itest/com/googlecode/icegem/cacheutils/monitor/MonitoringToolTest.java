package itest.com.googlecode.icegem.cacheutils.monitor;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googlecode.icegem.cacheutils.monitor.MonitoringTool;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;
import com.googlecode.icegem.utils.JavaProcessLauncher;

public class MonitoringToolTest {

    private static Process cacheServer1;
    /** Field cacheServer2  */
    private static Process cacheServer2;
    /** Field javaProcessLauncher  */
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

    @BeforeClass
    public void setUp() throws IOException, InterruptedException, TimeoutException {
        startCacheServers();
    }

    @AfterClass
    public void tearDown() throws IOException {
        stopCacheServers();
    }

	@Test
	public void testMain() throws Exception {
		MonitoringTool tool = new MonitoringTool();
		
		CountingNodeEventHandler handler = new CountingNodeEventHandler();
		tool.addNodeEventHandler(handler);
		
		tool.start();
		
		Thread.sleep(5 * 1000);
		
		assertThat(handler.getCount()).isEqualTo(4);
	}
	
    private void startCacheServers() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(Server.class);
        cacheServer2 = javaProcessLauncher.runWithConfirmation(Server.class);
    }

    private void stopCacheServers() throws IOException {
        javaProcessLauncher.stop(cacheServer1);
        javaProcessLauncher.stop(cacheServer2);
    }

}
