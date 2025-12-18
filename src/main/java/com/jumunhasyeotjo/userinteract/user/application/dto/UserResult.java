package com.jumunhasyeotjo.userinteract.user.application.dto;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResult (
    Long userId,
    String name,
    String slackId,
    String role,
    UUID belong,
    String status,
    LocalDateTime createdAt
) {
    public static UserResult from(User user) {
        return new UserResult(
            user.getUserId(),
            user.getName(),
            user.getSlackId(),
            user.getRole().name(),
            user.getBelong(),
            user.getStatus().name(),
            user.getCreatedAt()
        );
    }
}
