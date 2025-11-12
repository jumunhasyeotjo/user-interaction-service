package com.jumunhasyeotjo.userinteract.user.domain;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.service.UserDomainService;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDomainService userDomainService;

    @Test
    @DisplayName("COMPANY_DRIVER 승인 시 order가 정상 부여된다")
    void approveUserWithCompanyDriverWillSuccess() {
        // given
        UUID hubId = UUID.randomUUID();
        User user = User.join("user1", "password", "slack1", "COMPANY_DRIVER", hubId);
        when(userRepository.findMaxDriverOrderByHubId(any()))
            .thenReturn(10);

        // when
        userDomainService.approveUser(user, UserStatus.APPROVED);

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.APPROVED);
        assertThat(user.getDriver()).isNotNull();
        assertThat(user.getDriver().getDeliveryOrder()).isEqualTo(11);
        assertThat(user.getDriver().getHubId()).isEqualTo(hubId);
    }

    @Test
    @DisplayName("HUB_DRIVER는 승인 시 order가 정상 부여된다")
    void approveUserWithHubDriverWillSuccess() {
        // given
        UUID hubId = UUID.randomUUID();
        User user = User.join("user1", "password", "slack1", "HUB_DRIVER", hubId);
        when(userRepository.findMaxHubDriverOrder())
            .thenReturn(10);

        // when
        userDomainService.approveUser(user, UserStatus.APPROVED);

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.APPROVED);
        assertThat(user.getDriver()).isNotNull();
        assertThat(user.getDriver().getDeliveryOrder()).isEqualTo(11);
        assertThat(user.getDriver().getHubId()).isNull();
    }

    @Test
    @DisplayName("MASTER는 승인 대상이 아니므로 exception을 뱉는다.")
    void approveUserWithMasterWillThrowException() {
        // given
        UUID hubId = UUID.randomUUID();
        User user = User.join("user1", "password", "slack1", "MASTER", hubId);

        // when, then
        assertThatThrownBy(() -> userDomainService.approveUser(user, UserStatus.APPROVED))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.NOT_APPROVAL_TARGET.getMessage());
    }
}
