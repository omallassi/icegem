package integration;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.googlecode.icegem.message.barrier.core.RegionListeningBarrierBean;
import com.googlecode.icegem.message.barrier.model.Trade;
import com.googlecode.icegem.message.barrier.model.TradeMsg;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.support.MessageBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * User: Artem Kondratyev e-mail: kondratevae@gmail.com
 */
public class TestExpiration {

    @Test
    public void expiration() throws InterruptedException {
        Region tradeRgn = ctxt.getBean("entityRegion", Region.class);
        Region expiredMessageRegion = ctxt.getBean("expiredMessageRegion", Region.class);
        Region messageRegion = ctxt.getBean("messageRegion", Region.class);


        Trade trade1 = new Trade();
        TradeMsg msg1 = new TradeMsg();
        msg1.setTradeId((Long) trade1.getId());

        Trade trade2 = new Trade();
        TradeMsg msg2 = new TradeMsg();
        msg2.setTradeId((Long) trade2.getId());

        tradeRgn.put(trade1.getId(), trade1);
        tradeRgn.put(trade2.getId(), trade2);
        adapter1.putMessage(MessageBuilder.withPayload(msg1).setHeader("entityId", trade1.getId()).build());
        adapter1.putMessage(MessageBuilder.withPayload(msg2).setHeader("entityId", trade2.getId()).build());

        TimeUnit.SECONDS.sleep(7);

        Assert.assertEquals(2, expiredMessageRegion.keySetOnServer().size());
        Assert.assertEquals(0, messageRegion.values().size());
        TimeUnit.SECONDS.sleep(7);
        Assert.assertEquals(0, expiredMessageRegion.keySetOnServer().size());
        adapter1.close();
    }

    @Before
    public void init() throws InterruptedException, IOException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=test/expiration/expirationTest.properties"}, null);
        ctxt = new ClassPathXmlApplicationContext("test/expiration/expiration-config.xml");
        adapter1 = ctxt.getBean("adapter1", RegionListeningBarrierBean.class);
    }

    @After
    public void stop() throws IOException, InterruptedException {
        ctxt.getBean("client", ClientCache.class).close();
        javaProcessLauncher.stopByDestroyingProcess(cacheServer1);
//        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
        System.out.println("stopped cache sever");
    }

    private ApplicationContext ctxt;
    private RegionListeningBarrierBean adapter1;
    //private RegionListeningBarrierBean adapter2;
    private JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();
    private static Process cacheServer1;

}
