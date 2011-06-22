package itest.com.googlecode.icegem.cacheutils.signallistener;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.cacheutils.signallistener.WaitforTool;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;
//import org.junit.Ignore;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * User: Artem Kondratev kondratevae@gmail.com
 */

public class SignalListenerTest {

    private Process node;
    private Region signalRegion;
    private ClientCache clientCache;
    private JavaProcessLauncher launcher = new JavaProcessLauncher();

    @BeforeClass
    public void init() throws IOException, InterruptedException {
        node = launcher.runWithConfirmation(ServerTemplate.class,
                new String[] {"-DgemfirePropertyFile=signalListener.properties"},
                null);
        clientCache = new ClientCacheFactory()
                .set("log-level", "none")
                .set("cache-xml-file", "signal-client.xml")
                .create();
        signalRegion = clientCache.getRegion("signal-region");
        if (signalRegion == null)
            throw new NullPointerException("check your configuration, there is no \'signal-region\'");
        signalRegion.put("existedSignalKey", 0);
    }

    @Test
    public void signalAppeared() throws InterruptedException {
        int result = WaitforTool.waitSignal(signalRegion, "existedSignalKey", 5000, 1000);
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void signalTimeout() throws InterruptedException {
        int result = WaitforTool.waitSignal(signalRegion, "absentSignalKey", 5000, 1000);
        assertThat(result).isNotEqualTo(0);

    }

    @AfterClass
    public void close() throws IOException, InterruptedException {
        launcher.stopBySendingNewLineIntoProcess(node);
        if (clientCache != null)
            clientCache.close();
    }
}
