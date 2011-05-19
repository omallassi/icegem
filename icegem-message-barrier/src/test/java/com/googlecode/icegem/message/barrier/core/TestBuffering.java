package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.googlecode.icegem.message.barrier.model.Trade;
import com.googlecode.icegem.message.barrier.model.TradeMsg;
import com.googlecode.icegem.message.barrier.model.TradeState;
import org.junit.*;

import java.io.IOException;
import java.util.Collections;

/**
 * User: volcano
 */
@Ignore
public class TestBuffering extends TestParent{

    @Before
    public void init() throws IOException, InterruptedException {

        runServerSide(CacheServerMember.class, "test/del-msg-server-test.xml");

        client = new ClientCacheFactory()
                .set("cache-xml-file", "test/del-msg-adapter-test.xml")
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
        regionListeningBarrierBean.setCollectorTime(50);
        regionListeningBarrierBean.initExpiredMsgCollectors();
    }

    @Test
    public void DeleteOnChangeState() throws InterruptedException {
        Trade tr1 = new Trade();
        TradeMsg msg1 = new TradeMsg();
        msg1.setTradeId((Long) tr1.getId());

        tradeRgn.put(tr1.getId(), tr1);

        tr1.setState(TradeState.ACTIVE);
        tradeRgn.put(tr1.getId(), tr1);
        Thread.sleep(5000);

        Assert.assertNull(msgRgn.get(msg1.getId()));
        Assert.assertEquals(Collections.EMPTY_SET, msgSequenceRgn.keySet());
    }

    @After
    public void close() throws IOException{
        if (regionListeningBarrierBean != null)
            regionListeningBarrierBean.close();
        if (client != null)
            client.close();
        stopServerSide();
    }
}
