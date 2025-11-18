package com.jumunhasyeotjo.userinteract.auth.infrastructure.dto;

import com.jumunhasyeotjo.userinteract.user.application.command.JoinCommand;

import java.util.UUID;

public record JoinReq(
    String name,
    String password,
    String slackId,
    String role,
    UUID belong
) {
    public JoinCommand toCommand() {
        return new JoinCommand(name, password, slackId, role, belong);
    }
}
