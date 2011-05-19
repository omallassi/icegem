package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: akondratyev
 */
public class TouchEntryTask extends TimerTask{

    private static Logger logger = LoggerFactory.getLogger(TouchEntryTask.class);

    private Region messageRegion;
    private Region<Object, List> messageSequenceRegion;

    public TouchEntryTask(Region messageRegion, Region messageSequenceRegion) {
        this.messageRegion = messageRegion;
        this.messageSequenceRegion = messageSequenceRegion;
    }

    @Override
    public void run() {
        Set keys = new HashSet();
        for(List trades: messageSequenceRegion.values())
            keys.addAll(trades);
        if (keys.isEmpty())
            return;
        logger.trace("touch {}", keys);
        FunctionService.onRegion(messageRegion).withFilter(keys).execute(new TouchEntryFunction());
    }
}
