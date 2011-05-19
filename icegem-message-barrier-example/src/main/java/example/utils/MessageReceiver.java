package example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

/**
 * User: akondratyev
 */
public class MessageReceiver {

    private static Logger logger = LoggerFactory.getLogger(MessageReceiver.class);
    private int totalCount = 0;

    public void receive(Message msg) {
        totalCount++;
        if (totalCount % 1000 == 0)
            logger.info("now received msg: {}", totalCount);
        logger.debug("receive: {}", msg.getPayload());
    }
}
