package com.jumunhasyeotjo.userinteract.user.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record JoinCommand (
    String name,
    String password,
    String slackId,
    String role,
    UUID belong
){
}
