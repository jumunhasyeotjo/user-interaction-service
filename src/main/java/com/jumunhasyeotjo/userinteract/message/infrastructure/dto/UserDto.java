package com.jumunhasyeotjo.userinteract.message.infrastructure.dto;

import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    Long userId,
    String name,
    String slackId,
    String role,
    UUID belong,
    String status,
    LocalDateTime createdAt
) {
    public static UserDto fromResult(UserResult result) {
        return new UserDto(
            result.userId(),
            result.name(),
            result.slackId(),
            result.role(),
            result.belong(),
            result.status(),
            result.createdAt()
        );
    }
}
