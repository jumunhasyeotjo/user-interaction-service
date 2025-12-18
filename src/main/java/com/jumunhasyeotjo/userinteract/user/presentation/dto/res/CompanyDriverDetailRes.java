package com.jumunhasyeotjo.userinteract.user.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.user.application.dto.CompanyDriverResult;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyDriverDetailRes(
    Long userId,
    String name,
    String slackId,
    String role,
    String hubId,
    Integer deliveryOrder,
    String status,
    LocalDateTime createdAt
) {
    public static CompanyDriverDetailRes from(CompanyDriverResult user) {
        return new CompanyDriverDetailRes(
            user.userId(),
            user.name(),
            user.slackId(),
            user.role(),
            user.hubId().toString(),
            user.deliveryOrder(),
            user.status(),
            user.createdAt()
        );
    }
}
