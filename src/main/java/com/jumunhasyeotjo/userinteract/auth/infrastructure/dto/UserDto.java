package com.jumunhasyeotjo.userinteract.auth.infrastructure.dto;


import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;

import java.time.LocalDateTime;

public record UserDto(
    Long userId,
    String name,
    String slackId,
    String role,
    String status,
    LocalDateTime createdAt
) {
    public static UserDto from(UserResult userResult) {
        return new UserDto(
            userResult.userId(),
            userResult.name(),
            userResult.slackId(),
            userResult.role(),
            userResult.status(),
            userResult.createdAt()
        );
    }
}
