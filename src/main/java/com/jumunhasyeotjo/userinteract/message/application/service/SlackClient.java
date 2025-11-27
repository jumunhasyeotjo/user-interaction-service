package com.jumunhasyeotjo.userinteract.message.application.service;

import java.util.List;

public interface SlackClient {
    void sendMessage(List<String> userIds, String message);
}
