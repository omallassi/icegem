package example.utils;

import com.gemstone.gemfire.cache.Region;
import example.model.Trade;
import example.model.TradeMsg;
import example.model.TradeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TradeGenerator implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(TradeGenerator.class);

    private int tradeCount = 0;
    private Region tradeRgn;
    private Region msgRegion;
    private int msgCount;
    private int fromId;
    //private static ConcurrentMap tradeIds = new ConcurrentHashMap();
    private static Set<Object> tradeIds = new HashSet<Object>();

    public TradeGenerator() {
    }

    public TradeGenerator(int tradeCount, Region tradeRgn, Region msgRegion, int msgCount, int fromId) {
        this.tradeCount = tradeCount;
        this.tradeRgn = tradeRgn;
        this.msgRegion = msgRegion;
        this.msgCount = msgCount;
        this.fromId = fromId;
    }

    public void run() {
        Random rnd = new Random();
        //create trades
        Timer.start();
        for (int i = 0; i < tradeCount; i++) {
            Trade tr = new Trade();
            tr.setId(fromId + i);
            tradeRgn.put(tr.getId(), tr);
            //tradeIds.put(tr.getId(), 0);
            tradeIds.add(tr.getId());
        }
        logger.info("{} trades were created", tradeCount);
        logger.info("it takes {} ", Timer.estimate());

        //new Thread(new TradeMsgGenerator(msgRegion, msgCount)).start();
        pauseInSec(3);
        logger.info("start activating trades");

        //start changing states
        rnd = new Random();
        Set passedTrades = new HashSet();
        for (int i = 0; i < tradeCount; i++) {
            long id = (long) rnd.nextInt(tradeCount);
            if (passedTrades.contains(fromId + id))
                continue;
            Trade trade = (Trade) tradeRgn.get(fromId + id);
            trade.setState(TradeState.ACTIVE);
            //logger.trace("make trade {} active", trade.getId());
            tradeRgn.put(trade.getId(), trade);
            passedTrades.add(trade.getId());
            pauseInMillis(10);
        }
        logger.info("active trade count is: {}", passedTrades.size());
        //optional.. if random return one value more than one time
        int additionalActivated = 0;            //todo:remove
        for (Object tradeId : tradeIds) {
            if (passedTrades.contains(tradeId))
                continue;
            additionalActivated++;
            Trade trade = (Trade) tradeRgn.get(tradeId);
            trade.setState(TradeState.ACTIVE);
            //logger.trace("make trade {} active", trade.getId());
            tradeRgn.put(trade.getId(), trade);
        }
        logger.info("also activated {}", additionalActivated);
    }

    public int getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }

    public Region<Object, Trade> getTradeRgn() {
        return tradeRgn;
    }

    public void setTradeRgn(Region<Object, Trade> tradeRgn) {
        this.tradeRgn = tradeRgn;
    }

    public Region getMsgRegion() {
        return msgRegion;
    }

    public void setMsgRegion(Region msgRegion) {
        this.msgRegion = msgRegion;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    static class TradeMsgGenerator implements Runnable {
        private Region msgRegion;
        private int msgCount;

        TradeMsgGenerator(Region msgRegion, int msgCount) {
            this.msgRegion = msgRegion;
            this.msgCount = msgCount;
            logger.info("total msgs will be: {}", msgCount);
        }

        Random rnd = new Random();

        public void run() {
            pauseInMillis(200);
            Timer.start();
            for (int i = 0; i < msgCount; i++) {
                TradeMsg msg = new TradeMsg();

                msg.setTradeId(rnd.nextInt(tradeIds.size()));
                //logger.trace("adding msg on trade {}", msg.getTradeId());
                msgRegion.put(msg.getId(), msg);
            }
            logger.info("generating {} msgs takes {} ", new Object[] {msgCount, Timer.estimate()});
        }
    }

    public static void pauseInSec(int count) {
        try {
            TimeUnit.SECONDS.sleep(count);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public static void pauseInMillis(int count) {
        try {
            TimeUnit.MILLISECONDS.sleep(count);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Thread(new TradeGenerator()).start();
    }
}