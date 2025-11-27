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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final BCryptPasswordEncoder passwordEncoder;

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

        User savedUser = userRepository.save(user);
      
         return UserResult.from(savedUser);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "user", key = "#result.userId()"),
        @CacheEvict(value = "userByName", key = "#result.name()")
    })
    public UserResult approve(ApproveCommand approveCommand) {
        Long userId = approveCommand.userId();
        UserStatus status = UserStatus.of(approveCommand.status());
        User user = userRepository.findById(userId);
        userDomainService.approveUser(user, status);
        return UserResult.from(user);
    }

    @Cacheable(value = "user", key = "#userId")
    public UserResult getUser(Long userId) {
        User user = userRepository.findById(userId);
        return UserResult.from(user);
    }

    @Cacheable(value = "userByName", key = "#name")
    public UserResult getUserByName(String name) {
        User user = userRepository.findByName(name);
        return UserResult.from(user);
    }

    public Page<UserResult> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResult::from);
    }

    public Page<UserResult> getUsersByStatus(Pageable pageable, UserStatus status) {
        return userRepository.findAllByStatus(pageable, status).map(UserResult::from);
    }

    public Page<UserResult> getUsersByRole(Pageable pageable, UserRole role) {
        return userRepository.findAllByRole(pageable, role).map(UserResult::from);
    }

    public List<HubManagerResult> getHubManagerByHubId(UUID hubId) {
        return userRepository.findHubManagerByHubId(hubId)
            .stream().map(HubManagerResult::from).toList();
    }

    public List<CompanyManagerResult> getCompanyManagerByCompanyId(UUID companyId) {
        return userRepository.findCompanyManagerByCompanyId(companyId)
            .stream().map(CompanyManagerResult::from).toList();
    }

    public UserResult validatePassword(String name, String rawPassword) {
        User user = userRepository.findByName(name);
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return UserResult.from(user);
        } else {
            throw new BusinessException(ErrorCode.INVALID_USERINFO);
        }
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "user", key = "#result.userId()"),
        @CacheEvict(value = "userByName", key = "#result.name()")
    })
    public UserResult deleteUser(Long userId, Long requesterId) {
        User user = userRepository.findById(userId);
        user.markDeleted(requesterId); // 추후 수정 필요
        return UserResult.from(user);
    }
}
