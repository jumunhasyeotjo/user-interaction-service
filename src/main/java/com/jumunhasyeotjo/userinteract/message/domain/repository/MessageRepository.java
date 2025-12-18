package com.jumunhasyeotjo.userinteract.message.domain.repository;

import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    List<Message> saveAll(List<Message> messages);
    Message findById(UUID id);
    Page<Message> findAllByUserId(UserId userId, Pageable pageable);
}
