package example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;

/**
 * User: akondratyev
 */
public class MessageReceiver implements MessageHandler {

    private static Logger logger = LoggerFactory.getLogger(MessageReceiver.class);
    private int totalCount = 0;

    public void handleMessage(Message<?> message) throws MessagingException {
        totalCount++;
        if (totalCount % 1000 == 0) {
            logger.info("now received msg: {}", totalCount);
        }
        logger.trace("receive: {}", message.getPayload());
    }
}
