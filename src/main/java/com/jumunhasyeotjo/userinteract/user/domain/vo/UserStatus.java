package com.jumunhasyeotjo.userinteract.user.domain.vo;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;

import java.util.Arrays;

public enum UserStatus {
    PENDING,
    APPROVED,
    REJECTED,
    ;

    public static UserStatus of(String status) {
        return Arrays.stream(UserStatus.values())
            .filter(a -> a.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "유효한 ROLE이 아닙니다."));
    }

    public boolean canBeApproved() {
        return this == PENDING;
    }
}
