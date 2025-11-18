package com.jumunhasyeotjo.userinteract.message.infrastructure.repository;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.repository.MessageRepository;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageRepositoryAdaptor implements MessageRepository {

    private final JpaMessageRepository jpaRepository;

    @Override
    public Message save(Message message) {
        return jpaRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return jpaRepository.findById(id).orElseThrow(() ->
            new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Override
    public Page<Message> findAllByUserId(UserId userId, Pageable pageable) {
        return jpaRepository.findAllByUserId(userId, pageable);
    }
}
