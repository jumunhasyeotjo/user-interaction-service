package com.jumunhasyeotjo.userinteract.user.infrastructure.repository;

import com.jumunhasyeotjo.userinteract.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    boolean existsByName(String name);
}
