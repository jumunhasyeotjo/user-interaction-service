package com.jumunhasyeotjo.userinteract.user.domain.repository;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserRepository {
    User save(User user);
    User findById(Long id);
    User findByName(String name);
    boolean existsByName(String name);
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByStatus(Pageable pageable, UserStatus status);
    Page<User> findAllByRole(Pageable pageable, UserRole role);
    Page<User> findCompanyDriverByHubId(Pageable pageable, UUID hubId);
    Page<User> findHubDriverByHubId(Pageable pageable);
    Page<User> findHubManagerByHubId(Pageable pageable, UUID hubId);
    Page<User> findCompanyManagerByCompanyId(Pageable pageable, UUID companyId);
    Integer findMaxDriverOrderByHubId(UUID hubId);
    Integer findMaxHubDriverOrder();
}
