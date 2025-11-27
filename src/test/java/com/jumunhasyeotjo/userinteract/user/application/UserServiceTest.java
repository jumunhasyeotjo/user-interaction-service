package com.jumunhasyeotjo.userinteract.user.application;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.user.application.command.ApproveCommand;
import com.jumunhasyeotjo.userinteract.user.application.command.JoinCommand;
import com.jumunhasyeotjo.userinteract.user.application.dto.*;
import com.jumunhasyeotjo.userinteract.user.domain.entity.CompanyManager;
import com.jumunhasyeotjo.userinteract.user.domain.entity.Driver;
import com.jumunhasyeotjo.userinteract.user.domain.entity.HubManager;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.service.UserDomainService;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User user;

    private void setUser(String role) {
        user = User.join(
            "Alice",
            "password",
            "slackId",
            role,
            UUID.randomUUID()
        );
    }

    @Test
    @DisplayName("회원 가입 성공")
    void joinWillSuccess() {
        JoinCommand command = new JoinCommand("Alice", "password", "slackId", "HUB_DRIVER", UUID.randomUUID());
        setUser(UserRole.HUB_DRIVER.name());
        when(userRepository.existsByName("Alice")).thenReturn(false);

        when(userRepository.save(any())).thenAnswer(invocation -> {
            invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "userId", 1L);
            return user;
        });


        UserResult result = userService.join(command);

        assertEquals("Alice", result.name());
    }

    @Test
    @DisplayName("회원 가입 중복 이름 예외")
    void join_DuplicateNameWillThrowsException() {
        JoinCommand command = new JoinCommand("Alice", "password", "slackId", "HUB_DRIVER", UUID.randomUUID());
        when(userRepository.existsByName("Alice")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.join(command));
    }

    @Test
    @DisplayName("회원 승인 성공")
    void approveWillSuccess() {
        Long userId = 1L;
        ApproveCommand command = new ApproveCommand(userId, "APPROVED");
        setUser(UserRole.HUB_DRIVER.name());

        when(userRepository.findById(userId)).thenReturn(user);

        userService.approve(command);

        verify(userDomainService).approveUser(user, UserStatus.APPROVED);
    }

    @Test
    @DisplayName("단일 회원 조회 성공")
    void getUserWillSuccess() {
        Long userId = 1L;
        setUser(UserRole.HUB_DRIVER.name());
        when(userRepository.findById(userId)).thenReturn(user);

        UserResult result = userService.getUser(userId);

        assertNotNull(result);
    }

    @Test
    @DisplayName("모든 회원 페이지 조회 성공")
    void getUsersWillSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        setUser(UserRole.HUB_DRIVER.name());
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserResult> result = userService.getUsers(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("회원 상태별 페이지 조회 성공")
    void getUsersByStatusWillSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        UserStatus status = UserStatus.APPROVED;
        setUser(UserRole.HUB_DRIVER.name());
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAllByStatus(pageable, status)).thenReturn(page);

        Page<UserResult> result = userService.getUsersByStatus(pageable, status);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("회원 역할별 페이지 조회 성공")
    void getUsersByRoleWillSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        UserRole role = UserRole.HUB_DRIVER;
        setUser(UserRole.HUB_DRIVER.name());
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAllByRole(pageable, role)).thenReturn(page);

        Page<UserResult> result = userService.getUsersByRole(pageable, role);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteUserWillSuccess() {
        Long userId = 1L;
        setUser(UserRole.HUB_DRIVER.name());
        when(userRepository.findById(userId)).thenAnswer(invocation -> {
            invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "userId", 1L);
            return user;
        });

        userService.deleteUser(userId, 1L);

        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getDeletedBy()).isEqualTo(1L);
    }
}