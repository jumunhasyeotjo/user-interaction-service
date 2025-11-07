package com.jumunhasyeotjo.userinteract.user.infrastructure.repository;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.jumunhasyeotjo.userinteract.user.domain.entity.QDriver.driver;
import static com.jumunhasyeotjo.userinteract.user.domain.entity.QUser.user;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return jpaUserRepository.findById(id).orElseThrow(
            () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Override
    public User findByName(String name) {
        return jpaUserRepository.findByName(name).orElseThrow(
            () ->  new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Override
    public boolean existsByName(String name) {
        return jpaUserRepository.existsByName(name);
    }

    @Override
    public Integer findMaxDriverOrderByHubId(UUID hubId) {
        Integer result = queryFactory
            .select(driver.deliveryOrder.max().coalesce(0))
            .from(user)
            .join(user.driver, driver)
            .where(driver.hubId.eq(hubId))
            .fetchOne();

        return result != null ? result : 0;
    }

    @Override
    public Integer findMaxHubDriverOrder() {
        Integer result = queryFactory
            .select(driver.deliveryOrder.max().coalesce(0))
            .from(user)
            .join(user.driver, driver)
            .where(driver.hubId.isNull())
            .fetchOne();

        return result != null ? result : 0;
    }

    public void delete(User user, Long requesterId) {
        user.markDeleted(requesterId);
    }
}
