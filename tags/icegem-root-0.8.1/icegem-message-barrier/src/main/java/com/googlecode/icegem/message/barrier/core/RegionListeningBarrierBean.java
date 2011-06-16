package com.googlecode.icegem.message.barrier.core;

import com.gemstone.gemfire.cache.InterestResultPolicy;
import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.message.barrier.model.InnerMessage;
import com.googlecode.icegem.message.barrier.model.BarrierPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.annotation.Publisher;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * User: akondratyev
 */
public class RegionListeningBarrierBean {

    private static final Logger logger = LoggerFactory.getLogger(RegionListeningBarrierBean.class);

    private Region entityRegion;
    private Region messageRegion;
    private Region messageSequenceRegion;
    private Region expiredMessageRegion;
    private Region messageCheckRegion;
    private MessageChannel channel;
    private Thread internalTask;
    private Timer timer;
    private int collectorTime = 60;
    private int touchTime = 30;
    private BarrierPolicy barrierPolicy;
    public final static String NEXT_TO_POLL = "NEXT_TO_POLL";
    public final static String NEXT_ID = "NEXT_ID";

    public RegionListeningBarrierBean() {
        timer = new Timer();

    }

    /**
     * use putMessage(Message msg)
     * */
    @Deprecated
    public void putMessage(Object id, Object msg, Object entityId) {
        InnerMessage internalMsg = new InnerMessage(msg);
        internalMsg.setMessageId(id);
        internalMsg.setEntityId(entityId);
        logger.debug("put msg {}, {}", new Object[]{id, msg});
        messageRegion.putIfAbsent(id, internalMsg);
    }

    public void putMessage(Message msg) {
        InnerMessage internalMsg = new InnerMessage(msg);
        String id = msg.getHeaders().getId().toString();
        internalMsg.setMessageId(id);
        internalMsg.setEntityId(msg.getHeaders().get("entityId"));
        logger.debug("put msg {}, {}", new Object[]{id, msg});
        messageRegion.putIfAbsent(id, internalMsg);
    }

    public int getCollectorTime() {
        return collectorTime;
    }


    public Object isPollable(Message msg) {
        logger.debug("start checking msg {}", msg.getPayload());
        if (!barrierPolicy.isPollable(msg.getHeaders().get("entityId"))) {
            logger.debug("buffered msg {} ", msg);
            //putMessage(barrierPolicy.getMsgId(msg), msg,msg.getHeaders().get("entityId"));
            putMessage(msg);
            addMsgToSequence(msg.getHeaders().get("entityId"), msg.getHeaders().getId().toString());
            return null;
        }
        //delete msg
        if (messageRegion.containsKeyOnServer(msg.getHeaders().getId().toString())) {
            logger.debug("message is removed from system");
            messageRegion.remove(msg.getHeaders().getId().toString());
            delMsgFromSequence(msg.getHeaders().get("entityId"), msg.getHeaders().getId().toString());
        } else
            logger.debug("message comes to system first time and is polled at once");
        logger.debug("polled message {} ", msg);
        return msg;
    }

    @Publisher(channel = "toExternalSystem")
    public void popMessage(Message msg) {
        if (isPollable(msg) != null)
            channel.send(msg);
    }

    public Object retryChecking() {
        Lock nextToPoll = messageCheckRegion.getDistributedLock(NEXT_TO_POLL);

        try {
            Object keyMsgToRetry;
            nextToPoll.lock();
            if (!messageCheckRegion.containsValueForKey(messageCheckRegion.get(NEXT_TO_POLL))) {
                return null;
            }
            keyMsgToRetry = messageCheckRegion.get(messageCheckRegion.get(NEXT_TO_POLL));
            messageCheckRegion.remove(messageCheckRegion.get(NEXT_TO_POLL));
            long previousValue = (Long) messageCheckRegion.get(NEXT_TO_POLL);
            previousValue++;
            messageCheckRegion.put(NEXT_TO_POLL, previousValue);
            logger.debug("resend message to channel " + keyMsgToRetry);
            return ((InnerMessage) messageRegion.get(keyMsgToRetry)).getExternalMessage();
        } finally {
            nextToPoll.unlock();
        }
    }

    private void delMsgFromSequence(Object key, Object msgId) {
        List msgSequence;
        Lock lock = messageSequenceRegion.getDistributedLock(key);
        try {
            lock.lock();
            if (!messageSequenceRegion.containsKey(key)) {               //msg come to the system first time and pop at once
                logger.debug("{} doesn't present in messageSequenceRegion", key);
                return;
            }
            msgSequence = (List) messageSequenceRegion.get(key);
            msgSequence.remove(msgId);
            logger.debug("{} remove from messageSequenceRegion", msgId);
            if (msgSequence.isEmpty())
                messageSequenceRegion.destroy(key);
            else
                messageSequenceRegion.put(key, msgSequence);
        } finally {
            lock.unlock();
        }
    }

    private void addMsgToSequence(Object key, Object msgId) {
        List msgSequence;
        Lock lock = messageSequenceRegion.getDistributedLock(key);
        try {
            lock.lock();
            if (messageSequenceRegion.containsKey(key))
                msgSequence = (List) messageSequenceRegion.get(key);
            else
                msgSequence = new ArrayList();
            msgSequence.add(msgId);
            logger.debug("add msg {} to sequence on key {}", new Object[]{msgId, key});
            messageSequenceRegion.put(key, msgSequence);
        } finally {
            lock.unlock();
        }
    }


    public BarrierPolicy getBarrierPolicy() {
        return barrierPolicy;
    }

    public void setBarrierPolicy(BarrierPolicy barrierPolicy) {
        this.barrierPolicy = barrierPolicy;
    }
    /**
     * time for periodical collecting expired messages (in seconds)
     * */
    public void setCollectorTime(int collectorTime) {
        this.collectorTime = collectorTime;
        timer.schedule(new ExpiredMessageCollector(expiredMessageRegion, messageSequenceRegion, messageCheckRegion),
                TimeUnit.MILLISECONDS.convert(collectorTime, TimeUnit.SECONDS),
                TimeUnit.MILLISECONDS.convert(collectorTime, TimeUnit.SECONDS));
    }

    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }

    public int getTouchTime() {
        return touchTime;
    }
    /**
     * time for "touching" messages (in seconds)
     * */
    public void setTouchTime(int touchTime) {
        this.touchTime = touchTime;
        timer.schedule(new TouchEntryTask(messageRegion, messageSequenceRegion),
                TimeUnit.MILLISECONDS.convert(touchTime, TimeUnit.SECONDS),
                TimeUnit.MILLISECONDS.convert(touchTime, TimeUnit.SECONDS));
    }

    public void close() {
        if (internalTask != null)
            internalTask.interrupt();
        if (timer != null)
            timer.cancel();
    }

    public void setEntityRegion(Region entityRegion) {
        this.entityRegion = entityRegion;
        this.entityRegion.registerInterest("ALL_KEYS", InterestResultPolicy.KEYS_VALUES);
    }

    public void setMessageRegion(Region messageRegion) {
        this.messageRegion = messageRegion;
    }

    public void setMessageSequenceRegion(Region messageSequenceRegion) {
        this.messageSequenceRegion = messageSequenceRegion;
    }

    public void setExpiredMessageRegion(Region expiredMessageRegion) {
        this.expiredMessageRegion = expiredMessageRegion;
    }

    public void setMessageCheckRegion(Region messageCheckRegion) {
        this.messageCheckRegion = messageCheckRegion;
        messageCheckRegion.put(NEXT_ID, 0L);
        messageCheckRegion.put(NEXT_TO_POLL, 0L);
        internalTask = new Thread(new MessageReplyTask(messageCheckRegion));
        internalTask.setDaemon(true);
        internalTask.start();
    }

    //collect if some msg will expired
    @Deprecated
    public void initExpiredMsgCollectors() {
        timer.schedule(new ExpiredMessageCollector(expiredMessageRegion, messageSequenceRegion, messageCheckRegion),
                TimeUnit.MILLISECONDS.convert(collectorTime, TimeUnit.SECONDS),
                TimeUnit.MILLISECONDS.convert(collectorTime, TimeUnit.SECONDS));
        timer.schedule(new TouchEntryTask(messageRegion, messageSequenceRegion),
                TimeUnit.MILLISECONDS.convert(touchTime, TimeUnit.SECONDS),
                TimeUnit.MILLISECONDS.convert(touchTime, TimeUnit.SECONDS));
        logger.trace("saving region (for expired messages) will be checked every {} sec", collectorTime);
        logger.trace("barrier bean will \'touch\' it's messages every {} sec", touchTime);
    }

    /**
     *  gather messages (from messageToCheckRegion) to recheck for pop
     * */
    class MessageReplyTask implements Runnable {

        private Region messageCheckRegion;

        public MessageReplyTask(Region messageCheckRegion) {
            Assert.notNull(messageCheckRegion);
            this.messageCheckRegion = messageCheckRegion;
        }

        public void run() {

            while (true) {
                Lock nextToPoll = messageCheckRegion.getDistributedLock(NEXT_TO_POLL);
                try {
                    Object keyMsgToRetry;
                    nextToPoll.lock();
                    if (messageCheckRegion.containsValueForKey(messageCheckRegion.get(NEXT_TO_POLL))) {
                        keyMsgToRetry = messageCheckRegion.get(messageCheckRegion.get(NEXT_TO_POLL));
                        messageCheckRegion.remove(messageCheckRegion.get(NEXT_TO_POLL));
                        long previousValue = (Long) messageCheckRegion.get(NEXT_TO_POLL);
                        previousValue++;
                        messageCheckRegion.put(NEXT_TO_POLL, previousValue);
                        logger.debug("check msg once more " + keyMsgToRetry);
                        popMessage((Message) ((InnerMessage)messageRegion.get(keyMsgToRetry)).getExternalMessage());
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        logger.error("exception while replaying messages", e);
                    }
                } finally {
                    nextToPoll.unlock();
                }
            }
        }
    }
}




