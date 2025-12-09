package com.jumunhasyeotjo.userinteract.message.infrastructure.external;

import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.message.application.service.SlackClient;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import io.github.resilience4j.retry.annotation.Retry;
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
    @Retry(name = "slackRetry", fallbackMethod = "slackFallback")
    public void sendMessage(List<String> userIds, String message) {
        String channelId = openChannel(userIds);
        sendMessageToChannel(channelId, message);
    }

    private String openChannel(List<String> userIds) {
        try {
            var response = slack.methods(botToken).conversationsOpen(
                req -> req.users(userIds)
            );

            if (!response.isOk()) {
                log.error("Slack channel open failed: {}", response.getError());
                throw new RuntimeException(ErrorCode.GET_CHANNEL_FAILED.getMessage());
            }

            return response.getChannel().getId();

        } catch (SlackApiException | IOException e) {
            log.error("Slack API error during conversations.open", e);
            throw new RuntimeException(ErrorCode.GET_CHANNEL_FAILED.getMessage());
        }
    }

    private void sendMessageToChannel(String channelId, String message) {
        try {
            var response = slack.methods(botToken).chatPostMessage(
                req -> req.channel(channelId).text(message)
            );

            if (!response.isOk()) {
                log.error("Slack message send failed: {}", response.getError());
                throw new RuntimeException(ErrorCode.SEND_MESSAGE_FAILED.getMessage());
            }

        } catch (SlackApiException | IOException e) {
            log.error("Slack API error during chat.postMessage", e);
            throw new RuntimeException(ErrorCode.SEND_MESSAGE_FAILED.getMessage());
        }
    }

    private void slackFallback(List<String> userIds, String message, Throwable t) {
        log.error("Slack send failed after retries. userIds={}, error={}", userIds, t.getMessage());
    }
}
