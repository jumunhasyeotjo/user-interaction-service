package com.jumunhasyeotjo.userinteract.message.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.message.application.result.MessageResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageRes(
    UUID messageId,
    Long userId,
    String content,
    LocalDateTime createdAt
) {
    public static MessageRes from(MessageResult result) {
        return new MessageRes(
            result.messageId(),
            result.userId(),
            result.content(),
            result.createdAt()
        );
    }
}
