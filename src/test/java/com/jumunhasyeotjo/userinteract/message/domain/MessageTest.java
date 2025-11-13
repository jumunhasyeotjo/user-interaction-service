package com.jumunhasyeotjo.userinteract.message.domain;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class MessageTest {
    @Test
    @DisplayName("message 생성 성공")
    void createMessage() {
        Message message = Message.create(UserId.of(1L), Content.of("hi"));

        assertThat(message.getUserId())
            .isEqualTo(UserId.of(1L));
        assertThat(message.getContent())
            .isEqualTo(Content.of("hi"));
        assertThat(message.getMessageId())
            .isNull();
    }

    @Test
    @DisplayName("message 생성 실패 : userId fail")
    void createMessageWithWrongUserIdWillThrowException() {
        assertThatThrownBy(() -> Message.create(UserId.of(0L), Content.of("hi")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.INVALID_USER.getMessage());
    }

    @Test
    @DisplayName("message 생성 실패 : content fail")
    void createMessageWithWrongContentWillThrowException() {
        assertThatThrownBy(() -> Message.create(UserId.of(1L), Content.of("")))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.INVALID_CONTENT.getMessage());
    }
}
