package com.jumunhasyeotjo.userinteract.message.infrastructure;

import com.jumunhasyeotjo.userinteract.common.config.JpaAuditingConfig;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import com.jumunhasyeotjo.userinteract.message.infrastructure.repository.JpaMessageRepository;
import com.jumunhasyeotjo.userinteract.message.infrastructure.repository.MessageRepositoryAdaptor;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({MessageRepositoryAdaptor.class, JpaAuditingConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    @Autowired
    private JpaMessageRepository jpaMessageRepository;

    @Autowired
    private MessageRepositoryAdaptor messageRepositoryAdaptor;

    @Autowired
    private EntityManager em;

    private UUID messageId1;
    private UUID messageId2;

    @BeforeEach
    void setUp() {
        Message message1 = Message.create(UserId.of(1L), Content.of("hi"));
        Message message2 = Message.create(UserId.of(1L), Content.of("bye"));

        Message saved1 = messageRepositoryAdaptor.save(message1);
        Message saved2 = messageRepositoryAdaptor.save(message2);
        messageId1 = saved1.getMessageId();
        messageId2 = saved2.getMessageId();
        em.flush();
    }

    @Test
    @DisplayName("메시지 히스토리 저장")
    void saveWillSuccess() {
        Message message = Message.create(UserId.of(1L), Content.of("hi"));

        Message savedMessage = messageRepositoryAdaptor.save(message);

        assertThat(savedMessage.getMessageId()).isNotNull();
        assertThat(savedMessage.getContent()).isEqualTo(Content.of("hi"));
        assertThat(savedMessage.getUserId()).isEqualTo(UserId.of(1L));
    }

    @Test
    @DisplayName("메시지 단건 조회 성공")
    void findByIdWillSuccess() {

        Message findMessage = messageRepositoryAdaptor.findById(messageId1);

        assertThat(findMessage.getMessageId()).isEqualTo(messageId1);
        assertThat(findMessage.getContent()).isEqualTo(Content.of("hi"));
        assertThat(findMessage.getUserId()).isEqualTo(UserId.of(1L));
    }

    @Test
    @DisplayName("사용자 메시지 조회 성공")
    void findAllByUserIdWillSuccess() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Message> findMessages = messageRepositoryAdaptor.findAllByUserId(UserId.of(1L), pageable);

        assertThat(findMessages.getTotalElements()).isEqualTo(2);
        assertThat(findMessages.getTotalPages()).isEqualTo(1);
    }
}
