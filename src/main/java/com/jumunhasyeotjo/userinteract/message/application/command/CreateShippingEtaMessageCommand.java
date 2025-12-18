package com.jumunhasyeotjo.userinteract.message.application.command;

import java.util.UUID;

public record CreateShippingEtaMessageCommand(
    UUID originHubId,
    UUID receiverCompanyId,
    String orderIdMessage,
    String infoMessage,
    String etaMessage,
    Long driverId
) {
}
