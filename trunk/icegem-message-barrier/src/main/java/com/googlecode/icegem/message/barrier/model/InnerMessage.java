package com.googlecode.icegem.message.barrier.model;

import java.io.Serializable;

/**
 * represent message in system
 * User: akondratyev
 */
public class InnerMessage implements Serializable{

    public InnerMessage() {
    }

    public InnerMessage(Object externalMessage) {
        this.externalMessage = externalMessage;
    }

    public Object getId() {
        return messageId;
    }
    public Object getEntityId() {
        return  entityId;
    }

    public Object getExternalMessage() {
        return externalMessage;
    }

    public void setExternalMessage(Object externalMessage) {
        this.externalMessage = externalMessage;
    }


    public void setMessageId(Object messageId) {
        this.messageId = messageId;
    }

    public void setEntityId(Object entityId) {
        this.entityId = entityId;
    }

    private Object externalMessage;
    private Object messageId;
    private Object entityId;
}


