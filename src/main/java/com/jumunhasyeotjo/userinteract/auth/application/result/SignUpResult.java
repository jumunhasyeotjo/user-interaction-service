package com.jumunhasyeotjo.userinteract.auth.application.result;

import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDto;

import java.time.LocalDateTime;

public record SignUpResult(
    Long userId,
    String name,
    String slackId,
    String role,
    String status,
    LocalDateTime createdAt
) {
    public static SignUpResult from(UserDto dto) {
        return new SignUpResult(
            dto.userId(),
            dto.name(),
            dto.slackId(),
            dto.role(),
            dto.status(),
            dto.createdAt()
        );
    }
}
