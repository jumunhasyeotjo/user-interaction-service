package com.jumunhasyeotjo.userinteract.message.presentation.dto.req;

import com.jumunhasyeotjo.userinteract.message.application.command.MessageCreateCommand;

public record MessageCreateReq(
    Long userId,
    String content
) {
    public MessageCreateCommand toCommand() {
        return new MessageCreateCommand(userId, content);
    }
}
