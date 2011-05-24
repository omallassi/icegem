package com.googlecode.icegem.message.barrier.model;

import java.io.Serializable;

/**
 * User: akondratyev
 */
public class Trade implements Serializable{

    public Trade() {
        this.state = TradeState.RAW;
    }

    public TradeState getState() {
        return state;
    }

    public void setState(TradeState state) {
        this.state = state;
    }

    public Object getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "state='" + state + '\'' +
                ", id=" + id +
                '}';
    }

    private TradeState state;
    private long id = count++;
    private static long count = 0;
}
