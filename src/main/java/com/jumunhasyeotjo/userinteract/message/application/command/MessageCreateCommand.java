package com.jumunhasyeotjo.userinteract.message.application.command;

public record MessageCreateCommand (
    Long userId,
    String content
) {

}
