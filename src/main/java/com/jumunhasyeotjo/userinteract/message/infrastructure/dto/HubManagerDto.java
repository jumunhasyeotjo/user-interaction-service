package com.jumunhasyeotjo.userinteract.message.infrastructure.dto;

import com.jumunhasyeotjo.userinteract.user.application.dto.HubManagerResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record HubManagerDto(
    Long userId,
    String name,
    String slackId,
    String role,
    UUID hubId,
    String status,
    LocalDateTime createdAt
) {
    public static HubManagerDto fromResult(HubManagerResult result) {
        return new HubManagerDto(
            result.userId(),
            result.name(),
            result.slackId(),
            result.role(),
            result.hubId(),
            result.status(),
            result.createdAt()
        );
    }
}
