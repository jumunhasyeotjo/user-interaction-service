package com.jumunhasyeotjo.userinteract.message.domain.repository;

import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepository {
    Message save(Message message);
    Message findById(Long id);
    Page<Message> findAllByUserId(Long userId, Pageable pageable);
}
