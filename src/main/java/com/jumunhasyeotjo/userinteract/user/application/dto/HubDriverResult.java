package com.jumunhasyeotjo.userinteract.user.application.dto;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record HubDriverResult(
    Long userId,
    String name,
    String slackId,
    String role,
    Integer deliveryOrder,
    String status,
    LocalDateTime createdAt
) {
    public static HubDriverResult from(User user) {
        return new HubDriverResult(
            user.getUserId(),
            user.getName(),
            user.getSlackId(),
            user.getRole().name(),
            user.getDriver().getDeliveryOrder(),
            user.getStatus().name(),
            user.getCreatedAt()
        );
    }
}
