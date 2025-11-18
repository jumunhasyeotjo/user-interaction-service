package com.jumunhasyeotjo.userinteract.message.application;

import com.jumunhasyeotjo.userinteract.message.application.command.MessageCreateCommand;
import com.jumunhasyeotjo.userinteract.message.application.result.MessageResult;
import com.jumunhasyeotjo.userinteract.message.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.event.MessageCreatedEvent;
import com.jumunhasyeotjo.userinteract.message.domain.repository.MessageRepository;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final ApplicationEventPublisher publisher;
    private final MessageRepository messageRepository;
    private final UserClient userClient;

    @Transactional
    public void createMessage(MessageCreateCommand command) {
        UserId userId = UserId.of(command.userId());
        Content content = Content.of(command.content());

        Message message = Message.create(userId, content);
        messageRepository.save(message);

        String slackId = userClient.getSlackId(userId.getUserId());

        publisher.publishEvent(new MessageCreatedEvent(message, slackId));
    }

    public MessageResult getMessage(UUID messageId) {
        return MessageResult.from(messageRepository.findById(messageId));
    }

    public Page<MessageResult> getMessagesByUserId(Long userId, Pageable pageable) {
        return messageRepository.findAllByUserId(UserId.of(userId), pageable).map(MessageResult::from);
    }
}
