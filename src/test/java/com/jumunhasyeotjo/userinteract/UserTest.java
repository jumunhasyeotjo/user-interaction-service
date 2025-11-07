package com.jumunhasyeotjo.userinteract;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.user.domain.entity.Driver;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("유저 생성 시 상태가 승인 대기 중이어야 한다.")
    void joinUserStatusWillBePending() {
        // given
        String name = "user1";
        String password = "password1";
        String slackId = "email1";
        String role = "MASTER";
        UUID belong = UUID.randomUUID();

        // when
        User user = User.join(name, password, slackId, role, belong);

        // then
        assertThat(user.getStatus())
            .isEqualTo(UserStatus.PENDING);
    }

    @Test
    @DisplayName("유저 생성 시 유효하지 않은 role을 넣으면 에러를 반환한다.")
    void joinWithInvalidRoleWillThrowException() {
        // given
        String name = "user1";
        String password = "password1";
        String slackId = "email1";
        String role = "ADMIN";
        UUID belong = UUID.randomUUID();

        // when, then
        assertThatThrownBy(() -> User.join(name, password, slackId, role, belong))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("유효한 역할이 아닙니다.");
    }

    @Test
    @DisplayName("유저 생성 시 MASTER와 HUB_DRIVER는 소속이 존재하지 않는다.")
    void joinWithRoleMasterAndHubDriverWillDoesNotHaveBelonging() {
        // given
        String name1 = "user1";
        String password1 = "password1";
        String slackId1 = "email1";
        String role1 = "MASTER";
        UUID belong1 = UUID.randomUUID();

        String name2 = "user2";
        String password2 = "password2";
        String slackId2 = "email2";
        String role2 = "HUB_DRIVER";
        UUID belong2 = UUID.randomUUID();

        // when
        User user1 = User.join(name1, password1, slackId1, role1, belong1);
        User user2 = User.join(name2, password2, slackId2, role2, belong2);

        // then
        assertThat(user1.getBelong())
            .isNull();

        assertThat(user2.getBelong())
            .isNull();
    }

    @Test
    @DisplayName("HUB_DRIVER approve 성공 시나리오")
    void approveWithHubDriverWillSuccess() {
        // given
        String name = "user1";
        String password = "password1";
        String slackId = "email1";
        String role = "HUB_DRIVER";
        UUID belong = UUID.randomUUID();

        // when
        User user = User.join(name, password, slackId, role, belong);
        Driver driver = new Driver(user);
        user.approve(driver, UserStatus.APPROVED);

        // then
        assertThat(user.getStatus())
            .isEqualTo(UserStatus.APPROVED);

        assertThat(user.getDriver())
            .isEqualTo(driver);

        assertThat(driver.getUserId())
            .isEqualTo(user.getUserId());

        assertThat(driver.getUser())
            .isEqualTo(user);

        assertThat(driver.getHubId())
            .isNull();
    }

    @Test
    @DisplayName("COMPANY_DRIVER approve 성공 시나리오")
    void approveWithCompanyDriverWillSuccess() {
        // given
        String name = "user1";
        String password = "password1";
        String slackId = "email1";
        String role = "COMPANY_DRIVER";
        UUID belong = UUID.randomUUID();

        // when
        User user = User.join(name, password, slackId, role, belong);
        Driver driver = new Driver(user);
        user.approve(driver, UserStatus.APPROVED);

        // then
        assertThat(user.getStatus())
            .isEqualTo(UserStatus.APPROVED);

        assertThat(user.getDriver())
            .isEqualTo(driver);

        assertThat(driver.getUserId())
            .isEqualTo(user.getUserId());

        assertThat(driver.getUser())
            .isEqualTo(user);

        assertThat(driver.getHubId())
            .isEqualTo(user.getBelong());
    }


}
