package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.message.barrier.model.TradeMsg;
import com.googlecode.icegem.message.barrier.model.Trade;
import org.junit.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * User: akondratyev
 */
@Ignore
public class TestExpiration extends TestParent{

    @Before
    public void init() throws Exception{

        runServerSide(CacheServerMember.class, "test/expiration-server-test.xml");


        client = new ClientCacheFactory()
                .set("cache-xml-file", "test/expiration-adapter-test.xml")
                .create();
        tradeRgn = client.getRegion("trades");
        msgRgn = client.getRegion("msgs");
        expiredMsgRgn = client.getRegion("expired-msgs");
        msgSequenceRgn = client.getRegion("msgs-sequence");
        msgCheckRgn = client.getRegion("msg-to-check");
        regionListeningBarrierBean = new RegionListeningBarrierBean();
        regionListeningBarrierBean.setEntityRegion(tradeRgn);
        regionListeningBarrierBean.setMessageRegion(msgRgn);
        regionListeningBarrierBean.setMessageSequenceRegion(msgSequenceRgn);
        regionListeningBarrierBean.setExpiredMessageRegion(expiredMsgRgn);
        regionListeningBarrierBean.setMessageCheckRegion(msgCheckRgn);
        regionListeningBarrierBean.setTouchTime(15);
        regionListeningBarrierBean.setCollectorTime(10);
        regionListeningBarrierBean.initExpiredMsgCollectors();
    }

    @Test
    public void expiration() throws InterruptedException{
        Trade trade1  = new Trade();
        TradeMsg msg1 = new TradeMsg();
        msg1.setTradeId((Long) trade1.getId());


        Trade trade2 = new Trade();
        TradeMsg msg2 = new TradeMsg();
        msg2.setTradeId((Long) trade2.getId());

        tradeRgn.put(trade1.getId(), trade1);
        tradeRgn.put(trade2.getId(), trade2);
        regionListeningBarrierBean.putMessage(msg1.getId(), msg1, trade1.getId());
        regionListeningBarrierBean.putMessage(msg2.getId(), msg2, trade2.getId());

        TimeUnit.SECONDS.sleep(7);

        Assert.assertEquals(2, expiredMsgRgn.keySetOnServer().size());
        Assert.assertEquals(0, msgRgn.values().size());

        TimeUnit.SECONDS.sleep(7);
        Assert.assertEquals(0, expiredMsgRgn.keySetOnServer().size());
        for(Object key: msgRgn.keySetOnServer())
            Assert.assertNotNull(msgRgn.get(key));
    }

    @After
    public void closeClientConnection() throws IOException{
        regionListeningBarrierBean.close();
        if (client != null)
            client.close();
        stopServerSide();
    }
}
