package com.jumunhasyeotjo.userinteract.user.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.user.application.dto.HubDriverResult;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;

public record HubDriverDetailRes(
    Long userId,
    String name,
    String slackId,
    String role,
    Integer deliveryOrder,
    String status,
    LocalDateTime createdAt
) {
    public static HubDriverDetailRes from(HubDriverResult user) {
        return new HubDriverDetailRes(
            user.userId(),
            user.name(),
            user.slackId(),
            user.role(),
            user.deliveryOrder(),
            user.status(),
            user.createdAt()
        );
    }
}
