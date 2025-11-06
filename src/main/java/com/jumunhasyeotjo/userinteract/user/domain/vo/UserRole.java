package com.jumunhasyeotjo.userinteract.user.domain.vo;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    MASTER(Role.MASTER),
    HUB_MANAGER(Role.HUB_MANAGER),
    COMPANY_MANAGER(Role.COMPANY_MANAGER),
    HUB_DRIVER(Role.HUB_DRIVER),
    COMPANY_DRIVER(Role.COMPANY_DRIVER);

    private final String role;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
            .filter(a -> a.name().equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    private static class Role {
        public static final String MASTER = "ROLE_MASTER";
        public static final String HUB_MANAGER = "ROLE_HUB_MANAGER";
        public static final String COMPANY_MANAGER = "ROLE_COMPANY_MANAGER";
        public static final String HUB_DRIVER = "ROLE_HUB_DRIVER";
        public static final String COMPANY_DRIVER = "ROLE_COMPANY_DRIVER";
    }

    public boolean canBelongTo() {
        return this == UserRole.HUB_MANAGER
            || this == UserRole.COMPANY_MANAGER
            || this == UserRole.COMPANY_DRIVER;
    }

    public boolean isDriver() {
        return this == UserRole.COMPANY_DRIVER || this == UserRole.HUB_DRIVER;
    }
}
