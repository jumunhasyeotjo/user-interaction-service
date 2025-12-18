package com.jumunhasyeotjo.userinteract.user.application.dto;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record HubManagerResult(
    Long userId,
    String name,
    String slackId,
    String role,
    UUID hubId,
    String status,
    LocalDateTime createdAt
) {
    public static HubManagerResult from(User user) {
        return new HubManagerResult(
            user.getUserId(),
            user.getName(),
            user.getSlackId(),
            user.getRole().name(),
            user.getHubManager().getHubId(),
            user.getStatus().name(),
            user.getCreatedAt()
        );
    }
}
