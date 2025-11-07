package com.jumunhasyeotjo.userinteract.user.domain.service;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.user.domain.entity.*;
import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;

    public void approveUser(User user) {
        ApproveTarget target = createApprovalTarget(user);
        user.approve(target);

        if (user.getRole().isDriver()) {
            assignOrderToDriver(user.getRole(), (Driver) target);
        }
    }

    private void assignOrderToDriver(UserRole role, Driver driver) {
        if (role == UserRole.COMPANY_DRIVER) {
            int order = userRepository.findMaxDriverOrderByHubId(driver.getHubId());
            driver.assignOrder(order + 1);
        } else if (role == UserRole.HUB_DRIVER) {
            int order = userRepository.findMaxHubDriverOrder();
            driver.assignOrder(order + 1);
        }
    }

    private ApproveTarget createApprovalTarget(User user) {
        switch (user.getRole()) {
            case HUB_MANAGER -> {
                return new HubManager(user);
            }
            case COMPANY_DRIVER, HUB_DRIVER -> {
                return new Driver(user);
            }
            case COMPANY_MANAGER -> {
                return new CompanyManager(user);
            }
            default -> {
                throw new BusinessException(ErrorCode.NOT_APPROVAL_TARGET);
            }
        }
    }
}
