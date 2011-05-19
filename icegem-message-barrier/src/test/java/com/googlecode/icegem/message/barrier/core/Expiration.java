package com.googlecode.icegem.message.barrier.core;

import com.googlecode.icegem.message.barrier.core.plugins.BarrierExpiredMessageRegionListener;
import com.gemstone.gemfire.cache.CacheListener;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.message.barrier.model.Trade;
import com.googlecode.icegem.message.barrier.model.TradeMsg;
import org.junit.*;

import java.util.concurrent.TimeUnit;

/**
 * User: akondratyev
 */
@Ignore
public class Expiration extends TestParent{

    @Before
    public void init() {
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
        regionListeningBarrierBean.setTouchTime(10);
        regionListeningBarrierBean.setCollectorTime(3);
        regionListeningBarrierBean.initExpiredMsgCollectors();
    }

    @Test
    public void expiration() throws InterruptedException{
        Trade trade1  = new Trade();
        TradeMsg msg1 = new TradeMsg();
        msg1.setTradeId((Long) trade1.getId());

        Assert.assertEquals(trade1.getState(), "raw");
        Assert.assertEquals(trade1.getId(), msg1.getTradeId());

        Trade trade2 = new Trade();
        TradeMsg msg2 = new TradeMsg();
        msg2.setTradeId((Long) trade2.getId());

        Assert.assertEquals(trade2.getState(), "raw");
        Assert.assertEquals(trade2.getId(), msg2.getTradeId());

        tradeRgn.put(trade1.getId(), trade1);
        tradeRgn.put(trade2.getId(), trade2);
        regionListeningBarrierBean.putMessage(msg1.getId(), msg1, trade1.getId());
        regionListeningBarrierBean.putMessage(msg2.getId(), msg2, trade1.getId());

        TimeUnit.SECONDS.sleep(10);

        //todo: how can we define that no expiration occured?
        CacheListener listener = client.getRegion("expired-msgs").getAttributes().getCacheListeners()[0];

        Assert.assertEquals(0, ((BarrierExpiredMessageRegionListener) listener).getExpiredMsgCount());

    }

    @After
    public void closeClientConnection() {
        if (client != null)
            client.close();
    }
}
