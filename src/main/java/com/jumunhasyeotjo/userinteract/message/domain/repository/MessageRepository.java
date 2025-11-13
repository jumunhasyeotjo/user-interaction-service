package com.jumunhasyeotjo.userinteract.message.domain.repository;

import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Message findById(UUID id);
    Page<Message> findAllByUserId(UserId userId, Pageable pageable);
}
