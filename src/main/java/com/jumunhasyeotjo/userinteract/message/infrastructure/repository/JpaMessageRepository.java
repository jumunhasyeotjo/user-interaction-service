package com.jumunhasyeotjo.userinteract.message.infrastructure.repository;

import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaMessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findAllByUserId(UserId userId, Pageable pageable);
}
