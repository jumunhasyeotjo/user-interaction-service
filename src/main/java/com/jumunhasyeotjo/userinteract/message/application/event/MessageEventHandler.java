package com.jumunhasyeotjo.userinteract.message.application.event;

import com.jumunhasyeotjo.userinteract.message.application.service.SlackClient;
import com.jumunhasyeotjo.userinteract.message.domain.event.BulkMessagesCreatedEvent;
import com.jumunhasyeotjo.userinteract.message.domain.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

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
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageCreated(MessageCreatedEvent event) {
        slackClient.sendMessage(List.of(event.getSlackId()), event.getContent());
    }

    @Async
    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(
            delay = 500,
            random = true,
            maxDelay = 1000
        ),
        recover = "recoverBulk"
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBulkMessagesCreated(BulkMessagesCreatedEvent event) {
        slackClient.sendMessage(event.getSlackIds(), event.getContent());
    }

    @Recover
    public void recover(RuntimeException e, MessageCreatedEvent event) {
        log.warn("slack message retry failed : MessageId = {}", event.getMessageId(), e);
    }

    @Recover
    public void recoverBulk(RuntimeException e, BulkMessagesCreatedEvent event) {
        log.warn("error occurred", e);
        event.getMessages().forEach((message) -> {
            log.warn("slack message retry failed : MessageId = {}", message.getMessageId());
        });
    }
}
