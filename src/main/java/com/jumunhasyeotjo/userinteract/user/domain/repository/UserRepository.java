package com.jumunhasyeotjo.userinteract.user.domain.repository;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserRepository {
    User save(User user);
    User findById(Long id);
    User findByName(String name);
    boolean existsByName(String name);
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByStatusIsPENDING(Pageable pageable);
    Integer findMaxDriverOrderByHubId(UUID hubId);
    Integer findMaxHubDriverOrder();
}
