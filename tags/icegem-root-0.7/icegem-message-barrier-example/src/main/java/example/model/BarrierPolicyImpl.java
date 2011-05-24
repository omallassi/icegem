package example.model;

import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.message.barrier.model.BarrierPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

/**
 * User: Artem Kondratyev, e-mail: kondratevae@gmail.com
 */
public class BarrierPolicyImpl implements BarrierPolicy {
    private Logger logger = LoggerFactory.getLogger(BarrierPolicyImpl.class);
    private Region tradeRgn;

    public boolean isPollable(Message msg) {
        return false;
    }

    public void setTradeRgn(Region tradeRgn) {
        this.tradeRgn = tradeRgn;
    }

    public boolean isPollable(Object tradeId) {
        logger.debug("check on trade: {}", tradeId);
        if (!tradeRgn.containsKeyOnServer(tradeId))  {
            logger.trace("there's no such tradeId");
            return false;
        }
        Trade tr = (Trade) tradeRgn.get(tradeId);
        logger.debug("trade is " + tr.getState());
        return tr.getState() == TradeState.ACTIVE;
    }

    public synchronized Object getMsgId(Message msg) {
        return id++;
    }
    private static long id = 0;
}
