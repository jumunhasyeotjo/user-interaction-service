package com.jumunhasyeotjo.userinteract.message.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class MessageDomainEvent {
    private final LocalDateTime occurredAt;

    protected MessageDomainEvent() {
        this.occurredAt = LocalDateTime.now();
    }
}
