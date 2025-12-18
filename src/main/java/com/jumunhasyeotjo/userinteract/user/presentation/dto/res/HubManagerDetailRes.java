package com.jumunhasyeotjo.userinteract.user.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.user.application.dto.HubManagerResult;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record HubManagerDetailRes(
    Long userId,
    String name,
    String slackId,
    String role,
    String hubId,
    String status,
    LocalDateTime createdAt
) {
    public static HubManagerDetailRes from(HubManagerResult user) {
        return new HubManagerDetailRes(
            user.userId(),
            user.name(),
            user.slackId(),
            user.role(),
            user.hubId().toString(),
            user.status(),
            user.createdAt()
        );
    }
}
