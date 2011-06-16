package com.googlecode.icegem.message.barrier.model;

import org.springframework.integration.Message;

/**
 * interface for external system to check if message is pollable
 * User: akondratyev
 */
public interface BarrierPolicy {

    public boolean isPollable(Object tradeId);          //todo:remove

    public boolean isPollable(Message msg);

    public Object getMsgId(Message msg);
}
