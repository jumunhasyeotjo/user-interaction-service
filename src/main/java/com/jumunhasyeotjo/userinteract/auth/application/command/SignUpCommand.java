package com.jumunhasyeotjo.userinteract.auth.application.command;

import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.Role;

import java.util.UUID;

public record SignUpCommand(
    String name,
    String password,
    String slackId,
    Role role,
    UUID belong
){
}