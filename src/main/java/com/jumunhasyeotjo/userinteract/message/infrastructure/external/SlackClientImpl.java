package com.jumunhasyeotjo.userinteract.message.infrastructure.external;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.message.application.service.SlackClient;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class SlackClientImpl implements SlackClient {
    @Value("${slack.bot.token}")
    private String botToken;

    private final Slack slack = Slack.getInstance();

    @Override
    public void sendMessage(String userId, String message) {
        String channelId = openChannel(userId);
        if (channelId == null) {
            throw new BusinessException(ErrorCode.GET_CHANNEL_FAILED);
        }

        boolean response = sendMessageToChannel(channelId, message);

        if (!response) {
            log.error("Failed to send message");
            throw new BusinessException(ErrorCode.SEND_MESSAGE_FAILED);
        }
    }

    private String openChannel(String userId) {
        try {
            var response = slack.methods(botToken).conversationsOpen(
                req -> req.users(List.of(userId))
            );

            if (!response.isOk()) {
                log.error("Slack channel open failed: {}", response.getError());
                return null;
            }

            return response.getChannel().getId();
        } catch (SlackApiException | IOException e) {
            log.error("Slack API error in conversations.open", e);
            return null;
        }
    }

    private boolean sendMessageToChannel(String channelId, String message) {
        try {
            var response = slack.methods(botToken).chatPostMessage(
                req -> req.channel(channelId).text(message)
            );

            return response.isOk();
        } catch (SlackApiException | IOException e) {
            log.error("Slack API error in conversations.open", e);
            return false;
        }
    }
}
