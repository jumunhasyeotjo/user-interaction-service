package com.jumunhasyeotjo.userinteract.user.infrastructure;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.config.JPAQueryFactoryTestConfig;
import com.jumunhasyeotjo.userinteract.user.domain.entity.Driver;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import com.jumunhasyeotjo.userinteract.user.infrastructure.repository.JpaUserRepository;
import com.jumunhasyeotjo.userinteract.user.infrastructure.repository.UserRepositoryAdapter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({UserRepositoryAdapter.class, JPAQueryFactoryTestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    private User hubDriver;
    private User companyDriver;

    @BeforeEach
    void setup() {
        hubDriver = User.join("name1", "password1", "slackId1", "HUB_DRIVER", UUID.randomUUID());
        companyDriver = User.join("name2", "password2", "slackId2", "COMPANY_DRIVER", UUID.randomUUID());

        // Driver 까지 영속성 전이 될 수 있도록 해야 함.
        Driver hubDriverTarget = new Driver(hubDriver);
        Driver companyDriverTarget = new Driver(companyDriver);

        hubDriver.approve(hubDriverTarget, UserStatus.APPROVED);
        companyDriver.approve(companyDriverTarget, UserStatus.APPROVED);

        jpaUserRepository.save(hubDriver);
        jpaUserRepository.save(companyDriver);
    }

    @Test
    @DisplayName("정상적으로 사용자 조회")
    void findByNameWithExistNameWillSuccess() {
        User found = userRepositoryAdapter.findByName("name1");
        assertThat(found.getName()).isEqualTo("name1");
    }

    @Test
    @DisplayName("존재하지 않는 이름 조회")
    void findByNameWithNNotExistNameWillSuccess() {
        assertThatThrownBy(() -> userRepositoryAdapter.findByName("unknown"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("HUB DRIVER의 경우 hub_id를 통해 Order의 max 값을 가져온다. 현재의 테스트에서는 0이 나오는 게 맞다.")
    void findMaxDriverOrderByHubIdWithSuccessCase() {
        Integer result = userRepositoryAdapter.findMaxDriverOrderByHubId(companyDriver.getDriver().getHubId());
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("COMPANY DRIVER의 경우 hub_id가 null Order의 max 값을 가져온다.")
    void findMaxHubDriverOrderWithSuccessCase() {
        Integer result = userRepositoryAdapter.findMaxHubDriverOrder();
        assertThat(result).isEqualTo(0);
    }
}