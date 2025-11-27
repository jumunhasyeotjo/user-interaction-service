package com.jumunhasyeotjo.userinteract.message.presentation.dto.req;

import com.jumunhasyeotjo.userinteract.message.application.command.ShippingMessageCreateCommand;

import java.util.UUID;

public record ShippingMessageCreateReq(
    UUID originHubId,
    UUID receiverCompanyId,
    String orderIdMessage,
    String infoMessage,
    String etaMessage,
    Long driverId
) {
    public ShippingMessageCreateCommand toCommand() {
        return new ShippingMessageCreateCommand(
            this.originHubId,
            this.receiverCompanyId,
            this.orderIdMessage,
            this.infoMessage,
            this.etaMessage,
            this.driverId
        );
    }
}
