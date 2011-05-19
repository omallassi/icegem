package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: akondratyev
 */
public class ExpiredMessageCollector extends TimerTask{

    private static Logger logger = LoggerFactory.getLogger(ExpiredMessageCollector.class);

    private Region expiredMessageRegion;
    private Region messageSequenceRegion;
    private Region checkMessageRegion;

    public ExpiredMessageCollector(Region expiredMessageRegion, Region messageSequenceRegion, Region checkMessageRegion) {
        this.expiredMessageRegion = expiredMessageRegion;
        this.messageSequenceRegion = messageSequenceRegion;
        this.checkMessageRegion = checkMessageRegion;
    }

    @Override
    public void run() {
        Execution execution = FunctionService.onRegion(expiredMessageRegion);
        ResultCollector resultCollector = execution.execute(new ExpiredMessageCollectorFunction());
        List extractedMsg = (List) resultCollector.getResult();
        logger.trace("collected msgs from expired region {}", extractedMsg);
        if (!extractedMsg.isEmpty()) {
            logger.trace("result is {} ", extractedMsg.get(0));
            messageSequenceRegion.putAll((HashMap)extractedMsg.get(0));
            for(Object tradeId: ((HashMap) extractedMsg.get(0)).keySet()) {
                for (Object oneMsg: (List)((HashMap)extractedMsg.get(0)).get(tradeId))
                    checkMessageRegion.put(oneMsg, tradeId);                                //todo: !!!! we have another configuration NEXT_POLL as key
            }
        }
    }
}
