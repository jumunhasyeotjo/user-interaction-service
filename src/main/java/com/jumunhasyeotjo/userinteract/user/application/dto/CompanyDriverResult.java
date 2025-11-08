package com.jumunhasyeotjo.userinteract.user.application.dto;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyDriverResult(
    Long userId,
    String name,
    String slackId,
    String role,
    UUID hubId,
    Integer deliveryOrder,
    String status,
    LocalDateTime createdAt
) {
    public static CompanyDriverResult from(User user) {
        return new CompanyDriverResult(
            user.getUserId(),
            user.getName(),
            user.getSlackId(),
            user.getRole().name(),
            user.getDriver().getHubId(),
            user.getDriver().getDeliveryOrder(),
            user.getStatus().name(),
            user.getCreatedAt()
        );
    }
}
