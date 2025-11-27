package com.jumunhasyeotjo.userinteract.message.domain.event;

import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import lombok.Getter;

import java.util.List;

@Getter
public class BulkMessagesCreatedEvent extends MessageDomainEvent {
    private final List<Message> messages;
    private final List<String> slackIds;
    private final String content;

    public BulkMessagesCreatedEvent(List<Message> messages, List<String> slackIds, Content content) {
        super();
        this.messages = messages;
        this.slackIds = slackIds;
        this.content = content.getContent();
    }
}
