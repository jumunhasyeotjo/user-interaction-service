package com.jumunhasyeotjo.userinteract.message.infrastructure.event;

import com.jumunhasyeotjo.userinteract.message.application.command.CreateShippingDelayedMessageCommand;

import java.util.UUID;

public record ShippingDelayedMessageEvent(
    UUID shippingId,
    UUID receiverCompanyId,
    String message
) {
    public CreateShippingDelayedMessageCommand toCommand() {
        return new CreateShippingDelayedMessageCommand(
            receiverCompanyId,
            message
        );
    }
}
