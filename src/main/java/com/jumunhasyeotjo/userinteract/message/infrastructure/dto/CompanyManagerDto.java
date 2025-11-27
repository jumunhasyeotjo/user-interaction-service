package com.jumunhasyeotjo.userinteract.message.infrastructure.dto;

import com.jumunhasyeotjo.userinteract.user.application.dto.CompanyManagerResult;
import com.jumunhasyeotjo.userinteract.user.application.dto.HubManagerResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyManagerDto(
    Long userId,
    String name,
    String slackId,
    String role,
    UUID companyId,
    String status,
    LocalDateTime createdAt
) {
    public static CompanyManagerDto fromResult(CompanyManagerResult result) {
        return new CompanyManagerDto(
            result.userId(),
            result.name(),
            result.slackId(),
            result.role(),
            result.companyId(),
            result.status(),
            result.createdAt()
        );
    }
}
