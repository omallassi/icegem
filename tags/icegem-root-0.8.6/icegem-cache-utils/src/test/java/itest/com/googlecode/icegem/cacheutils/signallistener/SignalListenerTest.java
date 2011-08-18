package itest.com.googlecode.icegem.cacheutils.signallistener;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.cacheutils.signallistener.WaitforTool;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.PropertiesHelper;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 * User: Artem Kondratev kondratevae@gmail.com
 */

public class SignalListenerTest {

    private static Process node;
    private static Region signalRegion;
    private static ClientCache clientCache;
    private static JavaProcessLauncher launcher = new JavaProcessLauncher();

    @BeforeClass
    public static void init() throws IOException, InterruptedException {
        node = launcher.runWithConfirmation(ServerTemplate.class,
                new String[] {"-DgemfirePropertyFile=signalListener.properties"},
                null);

        PropertiesHelper properties = new PropertiesHelper("/signalListener.properties");

        clientCache = new ClientCacheFactory()
                .set("cache-xml-file", "signal-client.xml")
                .set("log-level", properties.getStringProperty("log-level"))
                .set("license-file", properties.getStringProperty("license-file"))
                .set("license-type", properties.getStringProperty("license-type"))
                .create();

        signalRegion = clientCache.getRegion("signal-region");
        if (signalRegion == null)
            throw new NullPointerException("check your configuration, there is no \'signal-region\'");
        signalRegion.put("existedSignalKey", 0);
    }

    @Test
    public void signalAppeared() throws InterruptedException {
        assertTrue(WaitforTool.waitSignal(signalRegion, "existedSignalKey", 5000, 1000));
    }

    @Test
    public void signalTimeout() throws InterruptedException {
        assertFalse(WaitforTool.waitSignal(signalRegion, "absentSignalKey", 5000, 1000));

    }

    @AfterClass
    public static void close() throws IOException, InterruptedException {
        launcher.stopBySendingNewLineIntoProcess(node);
        if (clientCache != null)
            clientCache.close();
    }
}
