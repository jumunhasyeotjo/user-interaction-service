package com.jumunhasyeotjo.userinteract.auth.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.auth.application.result.SignUpResult;

import java.time.LocalDateTime;

public record SignUpRes(
    Long userId,
    String name,
    String slackId,
    String role,
    String status,
    LocalDateTime createdAt
) {
    public static SignUpRes from(SignUpResult res) {
        return new SignUpRes(
            res.userId(),
            res.name(),
            res.slackId(),
            res.role(),
            res.status(),
            res.createdAt()
        );
    }
}
