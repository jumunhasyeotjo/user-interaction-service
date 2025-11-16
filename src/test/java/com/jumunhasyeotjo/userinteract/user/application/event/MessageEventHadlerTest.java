package com.jumunhasyeotjo.userinteract.user.application.event;

import com.jumunhasyeotjo.userinteract.message.application.event.MessageEventHandler;
import com.jumunhasyeotjo.userinteract.message.application.service.SlackClient;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.event.MessageCreatedEvent;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class MessageEventHandlerTest {

    @Mock
    private SlackClient slackClient;

    @InjectMocks
    private MessageEventHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(handler, "slackClient", slackClient);
    }

    @Test
    void handleMessageCreated_shouldCallSlackClient() throws InterruptedException {
        // given
        MessageCreatedEvent event = new MessageCreatedEvent(
            Message.create(UserId.of(1L), Content.of("hi")),
            "U12345"
        );

        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(slackClient).sendMessage(anyString(), anyString());

        handler.handleMessageCreated(event);

        latch.await(1, TimeUnit.SECONDS); // countdown 기다리기
        verify(slackClient, times(1)).sendMessage("U12345", "hi");
    }

    @Test
    void handleMessageCreated_shouldRetryAndRecover() {
        MessageCreatedEvent event = new MessageCreatedEvent(
            Message.create(UserId.of(1L), Content.of("hi")),
            "U12345"
        );

        doThrow(new RuntimeException("Slack API error"))
            .when(slackClient).sendMessage(anyString(), anyString());

        handler.recover(new RuntimeException("Slack API error"), event);

        verify(slackClient, times(0)).sendMessage(anyString(), anyString());
    }
}
