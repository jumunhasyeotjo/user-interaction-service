package com.jumunhasyeotjo.userinteract.message.application.event;

import com.jumunhasyeotjo.userinteract.message.application.service.SlackClient;
import com.jumunhasyeotjo.userinteract.message.domain.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventHandler {

    private final SlackClient slackClient;

    @Async
    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(
            delay = 500,
            random = true,
            maxDelay = 1000
        ),
        recover = "recover"
    )
    @EventListener
    public void handleMessageCreated(MessageCreatedEvent event) {
        slackClient.sendMessage(event.getSlackId(), event.getContent());
    }

    @Recover
    public void recover(Exception e, MessageCreatedEvent event) {
        log.warn("slack message retry failed : MessageId = {}", event.getMessageId(), e);
    }
}
