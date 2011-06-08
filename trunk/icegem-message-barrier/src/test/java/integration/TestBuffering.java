package integration;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.message.barrier.core.RegionListeningBarrierBean;
import com.googlecode.icegem.message.barrier.model.Trade;
import com.googlecode.icegem.message.barrier.model.TradeMsg;
import com.googlecode.icegem.message.barrier.model.TradeState;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Collections;

/**
 * User: Artem Kondratyev e-mail: kondratevae@gmail.com
 */
public class TestBuffering {

    @Test
    public void DeleteOnChangeState() throws InterruptedException {
        Region tradeRegion  = ctxt.getBean("entityRegion", Region.class);
        Region messageRegion = ctxt.getBean("messageRegion", Region.class);
        Region messageSequenceRegion = ctxt.getBean("messageSequenceRegion", Region.class);

        Trade tr1 = new Trade();
        TradeMsg msg1 = new TradeMsg();
        msg1.setTradeId((Long) tr1.getId());

        tradeRegion.put(tr1.getId(), tr1);

        tr1.setState(TradeState.ACTIVE);
        tradeRegion.put(tr1.getId(), tr1);
        Thread.sleep(5000);

        Assert.assertNull(messageRegion.get(msg1.getId()));
        Assert.assertEquals(0, messageSequenceRegion.keySet().size());

        adapter1.close();
    }

    @Before
    public void init() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=test/pollingmessage/polling.properties"}, null);
        ctxt = new ClassPathXmlApplicationContext("test/pollingmessage/polling-msg-test.xml");
        adapter1 = ctxt.getBean("adapter1", RegionListeningBarrierBean.class);
    }


    @After
    public void close() throws IOException, InterruptedException {
        ctxt.getBean("client", ClientCache.class).close();
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
    }

    private ApplicationContext ctxt;
    private RegionListeningBarrierBean adapter1;
    //private RegionListeningBarrierBean adapter2;
    private JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();
    private static Process cacheServer1;
}
