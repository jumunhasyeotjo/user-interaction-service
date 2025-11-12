package com.jumunhasyeotjo.userinteract.user.application.command;

public record ApproveCommand(
    Long userId,
    String status
) {
}
