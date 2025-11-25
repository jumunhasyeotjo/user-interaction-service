package com.jumunhasyeotjo.userinteract.message.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumunhasyeotjo.userinteract.common.ApiRes;
import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.application.result.MessageResult;
import com.jumunhasyeotjo.userinteract.message.domain.entity.Message;
import com.jumunhasyeotjo.userinteract.message.domain.vo.Content;
import com.jumunhasyeotjo.userinteract.message.domain.vo.UserId;
import com.jumunhasyeotjo.userinteract.message.presentation.controller.MessageController;
import com.jumunhasyeotjo.userinteract.message.presentation.dto.req.MessageCreateReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MessageService messageService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("POST /v1/messages - 메시지 생성 성공")
    void createMessageWillSuccess() throws Exception {
        // given
        MessageCreateReq req = new MessageCreateReq(1L, "hi");

        // when & then
        mockMvc.perform(post("/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isCreated());

        verify(messageService, times(1))
            .createMessage(any());
    }

    @Test
    @DisplayName("GET /v1/messages/{messageId} - 단건 조회 성공")
    void getMessageWillSuccess() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        Message message = Message.create(UserId.of(1L), Content.of("hi"));
        when(messageService.getMessage(messageId)).thenReturn(MessageResult.from(message));

        // when & then
        mockMvc.perform(get("/v1/messages/" + messageId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").value("hi"))
            .andExpect(jsonPath("$.data.userId").value(1L));
    }

    @Test
    @DisplayName("GET /v1/messages/user/{userId} - 사용자 메시지 조회 성공")
    void getMessagesWillSuccess() throws Exception {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createAt"));

        Message m1 = Message.create(UserId.of(1L), Content.of("hello"));
        Message m2 = Message.create(UserId.of(1L), Content.of("bye"));

        Page<Message> page = new PageImpl<>(List.of(m1, m2), pageable, 2);

        when(messageService.getMessagesByUserId(eq(userId), any(Pageable.class)))
            .thenReturn(page.map(MessageResult::from));

        // when & then
        mockMvc.perform(get("/v1/messages/user/{userId}", userId)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createAt,asc")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content.length()").value(2))
            .andExpect(jsonPath("$.data.totalElements").value(2))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }
}
