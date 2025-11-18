package com.jumunhasyeotjo.userinteract.message.application.service;

public interface SlackClient {
    void sendMessage(String userId, String message);
}
