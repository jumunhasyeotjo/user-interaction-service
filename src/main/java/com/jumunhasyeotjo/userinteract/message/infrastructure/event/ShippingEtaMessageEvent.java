package com.jumunhasyeotjo.userinteract.message.infrastructure.event;

import com.jumunhasyeotjo.userinteract.message.application.command.CreateShippingEtaMessageCommand;

import java.util.UUID;

public record ShippingEtaMessageEvent(
    UUID originHubId,
    UUID receiverCompanyId,
    String orderIdMessage,
    String infoMessage,
    String etaMessage,
    Long driverId
) {
    public CreateShippingEtaMessageCommand toCommand() {
        return new CreateShippingEtaMessageCommand(
            this.originHubId,
            this.receiverCompanyId,
            this.orderIdMessage,
            this.infoMessage,
            this.etaMessage,
            this.driverId
        );
    }
}
