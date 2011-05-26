package example.utils;

import example.model.TradeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.Publisher;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.support.MessageBuilder;

import java.lang.annotation.Annotation;
import java.util.Random;

/**
 * User: akondratyev
 */
public class MessageGenerator {

    private Logger logger = LoggerFactory.getLogger(MessageGenerator.class);

    private int MAX_TRADE_ID;
    private int MAX_MSG_COUNT;
    private static Random rnd;
    private int msgCount = 0;
    private MessageChannel channel;

    public MessageGenerator() {
        rnd = new Random();
    }

    public void setMAX_TRADE_ID(int MAX_TRADE_ID) {
        this.MAX_TRADE_ID = MAX_TRADE_ID;
    }

    public void setMAX_MSG_COUNT(int MAX_MSG_COUNT) {
        this.MAX_MSG_COUNT = MAX_MSG_COUNT;
    }

    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }

    @Publisher(channel = "fromTradeSystem")
    public void generate() {
        while(msgCount++ < MAX_MSG_COUNT) {
            TradeMsg msg = new TradeMsg();
            long tradeId = rnd.nextInt(MAX_TRADE_ID);
            msg.setTradeId(tradeId);
            logger.trace("gen msg");
            channel.send(MessageBuilder.withPayload(msg).setHeader("entityId", tradeId).build());
        }
        logger.info("messages were generated: " + msgCount);
    }

}
