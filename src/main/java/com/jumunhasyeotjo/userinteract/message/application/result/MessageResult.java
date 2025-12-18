package com.jumunhasyeotjo.userinteract.message.application.result;

import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResult(
    UUID messageId,
    Long userId,
    String content,
    LocalDateTime createdAt
) {
    public static MessageResult from(Message message) {
        return new MessageResult(
            message.getMessageId(),
            message.getUserId().getUserId(),
            message.getContent().getContent(),
            message.getCreatedAt()
        );
    }
}
