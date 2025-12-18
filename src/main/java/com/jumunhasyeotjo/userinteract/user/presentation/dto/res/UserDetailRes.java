package com.jumunhasyeotjo.userinteract.user.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;

import java.time.LocalDateTime;

public record UserDetailRes (
    Long userId,
    String name,
    String slackId,
    String role,
    String status,
    LocalDateTime createdAt
) {
    public static UserDetailRes from(UserResult user) {
        return new UserDetailRes(
            user.userId(),
            user.name(),
            user.slackId(),
            user.role(),
            user.status(),
            user.createdAt()
        );
    }
}
