package com.jumunhasyeotjo.userinteract.message.application.command;

import java.util.UUID;

public record CreateShippingDelayedMessageCommand(
    UUID companyId,
    String message
) {
}
