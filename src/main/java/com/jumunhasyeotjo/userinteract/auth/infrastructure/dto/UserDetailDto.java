package com.jumunhasyeotjo.userinteract.auth.infrastructure.dto;


import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDetailDto(
    Long userId,
    String name,
    String slackId,
    String role,
    UUID belong,
    String status,
    LocalDateTime createdAt
) {
    public static UserDetailDto from(UserResult userResult) {
        return new UserDetailDto(
            userResult.userId(),
            userResult.name(),
            userResult.slackId(),
            userResult.role(),
            userResult.belong(),
            userResult.status(),
            userResult.createdAt()
        );
    }
}
