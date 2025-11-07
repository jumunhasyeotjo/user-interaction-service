package com.jumunhasyeotjo.userinteract.user.domain.repository;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository {
    User save(User user);
    User findById(Long id);
    User findByName(String name);
    boolean existsByName(String name);
    Integer findMaxDriverOrderByHubId(UUID hubId);
    Integer findMaxHubDriverOrder();
}
