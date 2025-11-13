package com.jumunhasyeotjo.userinteract.message.domain.event;

import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;

import java.util.UUID;

public class MessageCreatedEvent extends MessageDomainEvent {
    private final UUID messageId;
    private final String slackId;
    private final String content;

    public MessageCreatedEvent(Message message, String slackId) {
        super();
        this.messageId = message.getMessageId();
        this.slackId = slackId;
        this.content = message.getContent().getContent();
    }
}
