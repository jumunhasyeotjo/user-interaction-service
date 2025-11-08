package com.jumunhasyeotjo.userinteract.user.application;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.user.application.command.ApproveCommand;
import com.jumunhasyeotjo.userinteract.user.application.command.JoinCommand;
import com.jumunhasyeotjo.userinteract.user.application.dto.*;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.service.UserDomainService;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;

    @Transactional
    public UserResult join(JoinCommand joinCommand) {
        if (userRepository.existsByName(joinCommand.name())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USER_NAME);
        }

         User user = User.join(
            joinCommand.name(),
            joinCommand.password(),
            joinCommand.slackId(),
            joinCommand.role(),
            joinCommand.belong()
        );

         return UserResult.from(userRepository.save(user));
    }

    @Transactional
    public UserResult approve(ApproveCommand approveCommand) {
        Long userId = approveCommand.userId();
        UserStatus status = UserStatus.of(approveCommand.status());
        User user = userRepository.findById(userId);
        userDomainService.approveUser(user, status);
        return UserResult.from(user);
    }

    @Transactional(readOnly = true)
    public UserResult getUser(Long userId) {
        User user = userRepository.findById(userId);
        return UserResult.from(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResult> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResult::from);
    }

    @Transactional(readOnly = true)
    public Page<UserResult> getUsersByStatus(Pageable pageable, UserStatus status) {
        return userRepository.findAllByStatus(pageable, status).map(UserResult::from);
    }

    @Transactional(readOnly = true)
    public Page<UserResult> getUsersByRole(Pageable pageable, UserRole role) {
        return userRepository.findAllByRole(pageable, role).map(UserResult::from);
    }

    @Transactional(readOnly = true)
    public Page<CompanyDriverResult> getCompanyDriverByHubId(Pageable pageable, UUID hubId) {
        return userRepository.findCompanyDriverByHubId(pageable, hubId).map(CompanyDriverResult::from);
    }

    @Transactional(readOnly = true)
    public Page<HubDriverResult> getHubDriverByHubId(Pageable pageable) {
        return userRepository.findHubDriverByHubId(pageable).map(HubDriverResult::from);
    }

    @Transactional(readOnly = true)
    public Page<HubManagerResult> getHubManagerByHubId(Pageable pageable, UUID hubId) {
        return userRepository.findHubManagerByHubId(pageable, hubId).map(HubManagerResult::from);
    }

    @Transactional(readOnly = true)
    public Page<CompanyManagerResult> getCompanyManagerByCompanyId(Pageable pageable, UUID companyId) {
        return userRepository.findCompanyManagerByCompanyId(pageable, companyId).map(CompanyManagerResult::from);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        user.markDeleted(1L); // 추후 수정 필요
    }
}
