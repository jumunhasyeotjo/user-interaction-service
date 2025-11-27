package com.jumunhasyeotjo.userinteract.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumunhasyeotjo.userinteract.user.application.UserService;
import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;
import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.service.UserDomainService;
import com.jumunhasyeotjo.userinteract.user.presentation.controller.UserController;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.req.ApproveReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean
    UserDomainService userDomainService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("PATCH /v1/users/approve 사용자 승인")
    void approveUserWillSuccess() throws Exception {
        // given
        ApproveReq req = new ApproveReq(
            1L,
            "PENDING"
        );

        UserResult mockResult = new UserResult(
            1L,
            "홍길동",
            "slack1",
            "HUB_DRIVER",
            "APPROVED",
            LocalDateTime.now()
        );

        BDDMockito.given(userService.approve(any())).willReturn(mockResult);

        // when, then
        mockMvc.perform(patch("/v1/users/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("홍길동"))
            .andExpect(jsonPath("$.data.status").value("APPROVED"));


    }


    @Test
    @DisplayName("GET /v1/users/{userId} - 단일 사용자 조회 성공")
    void getUserWillSuccess() throws Exception {
        // given
        UserResult mockResult = new UserResult(
            1L,
            "김철수",
            "slack999",
            "HUB_MANAGER",
            null,
            null
        );

        BDDMockito.given(userService.getUser(1L)).willReturn(mockResult);

        // when, then
        mockMvc.perform(get("/v1/users/{userId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("김철수"))
            .andExpect(jsonPath("$.data.role").value("HUB_MANAGER"));
    }

    @Test
    @DisplayName("DELETE /v1/users/{userId} - 사용자 삭제 성공")
    void deleteUserWillSuccess() throws Exception {
        // given
        UserResult mockResult = new UserResult(
            1L,
            "홍길동",
            "slack1",
            "HUB_DRIVER",
            "PENDING",
            LocalDateTime.now()
        );
        BDDMockito.given(userService.deleteUser(1L, 1L)).willReturn(mockResult);

        // when, then
        mockMvc.perform(delete("/v1/users/{userId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("홍길동"))
            .andExpect(jsonPath("$.data.role").value("HUB_DRIVER"));
    }
}