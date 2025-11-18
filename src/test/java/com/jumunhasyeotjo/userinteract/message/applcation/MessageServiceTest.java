package com.jumunhasyeotjo.userinteract.message.applcation;

import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.application.command.MessageCreateCommand;
import com.jumunhasyeotjo.userinteract.message.application.result.MessageResult;
import com.jumunhasyeotjo.userinteract.message.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.repository.MessageRepository;
import com.jumunhasyeotjo.userinteract.message.domain.event.MessageCreatedEvent;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("메시지 생성 성공 - 이벤트 발행 포함")
    void createMessageWillSuccess() {
        // given
        Long userId = 10L;
        String content = "Hello!";
        MessageCreateCommand command = new MessageCreateCommand(userId, content);

        Message message = Message.create(UserId.of(userId), Content.of(content));

        when(messageRepository.save(any(Message.class)))
            .thenReturn(message);
        when(userClient.getSlackId(userId))
            .thenReturn("SLACK123");

        // when
        messageService.createMessage(command);

        // then
        verify(messageRepository).save(any(Message.class));
        verify(userClient).getSlackId(userId);
        verify(publisher).publishEvent(any(MessageCreatedEvent.class));
    }

    @Test
    @DisplayName("메시지 단건 조회 성공")
    void getMessageWillSuccess() {
        UUID messageId = UUID.randomUUID();

        Message message = Message.create(UserId.of(1L), Content.of("hi"));
        when(messageRepository.findById(messageId)).thenReturn(message);

        MessageResult result = messageService.getMessage(messageId);

        assertEquals(1L, result.userId());
        assertEquals("hi", result.content());
    }


    @Test
    @DisplayName("특정 유저의 메시지 목록 조회 성공")
    void getMessagesByUserIdWillSuccess() {
        Long userId = 5L;
        Pageable pageable = PageRequest.of(0, 10);

        Message message = Message.create(UserId.of(userId), Content.of("hey"));
        Page<Message> messagePage = new PageImpl<>(List.of(message));

        when(messageRepository.findAllByUserId(UserId.of(userId), pageable))
            .thenReturn(messagePage);

        Page<MessageResult> result = messageService.getMessagesByUserId(userId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("hey", result.getContent().get(0).content());
        verify(messageRepository).findAllByUserId(UserId.of(userId), pageable);
    }
}