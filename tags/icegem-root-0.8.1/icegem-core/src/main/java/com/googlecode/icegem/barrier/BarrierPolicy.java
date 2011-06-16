package com.googlecode.icegem.barrier;

import java.io.Serializable;
import java.util.List;

/**
 * Strategy defining conditions of message pass-through and .  
 * @author akondratyev
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 */
public interface BarrierPolicy {

    public boolean canProceed(Serializable msg);

    public List<Serializable> getTrackedEntities(Serializable msg);

    public String getMessageId(Serializable msg);
}
