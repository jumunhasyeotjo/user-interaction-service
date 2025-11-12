package com.jumunhasyeotjo.userinteract.user.application.dto;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyManagerResult(
    Long userId,
    String name,
    String slackId,
    String role,
    UUID companyId,
    String status,
    LocalDateTime createdAt
) {
    public static CompanyManagerResult from(User user) {
        return new CompanyManagerResult(
            user.getUserId(),
            user.getName(),
            user.getSlackId(),
            user.getRole().name(),
            user.getCompanyManager().getCompanyId(),
            user.getStatus().name(),
            user.getCreatedAt()
        );
    }
}
