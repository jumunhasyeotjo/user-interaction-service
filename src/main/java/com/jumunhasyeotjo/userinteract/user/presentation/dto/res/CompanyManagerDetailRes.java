package com.jumunhasyeotjo.userinteract.user.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.user.application.dto.CompanyManagerResult;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyManagerDetailRes(
    Long userId,
    String name,
    String slackId,
    String role,
    String companyId,
    String status,
    LocalDateTime createdAt
) {
    public static CompanyManagerDetailRes from(CompanyManagerResult user) {
        return new CompanyManagerDetailRes(
            user.userId(),
            user.name(),
            user.slackId(),
            user.role(),
            user.companyId().toString(),
            user.status(),
            user.createdAt()
        );
    }
}
