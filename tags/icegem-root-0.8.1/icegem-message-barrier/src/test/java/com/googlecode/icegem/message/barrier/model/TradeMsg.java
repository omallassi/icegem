package com.googlecode.icegem.message.barrier.model;

import java.io.Serializable;

/**
 * User: akondratyev
 */
public class TradeMsg implements Serializable{
    public TradeMsg() {
    }

    public Object getId() {
        return id;
    }


    public Object getEntityId() {
        return tradeId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    @Override
    public String toString() {
        return "TradeMsg{" +
                "tradeId=" + tradeId +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeMsg tradeMsg = (TradeMsg) o;

        if (id != tradeMsg.id) return false;
        if (tradeId != tradeMsg.tradeId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (tradeId ^ (tradeId >>> 32));
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    private long tradeId;
    private static long count = 0;
    private long id = count++;
}
