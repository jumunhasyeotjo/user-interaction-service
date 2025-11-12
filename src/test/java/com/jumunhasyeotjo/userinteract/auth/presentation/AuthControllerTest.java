package com.jumunhasyeotjo.userinteract.auth.presentation;

import com.jumunhasyeotjo.userinteract.auth.application.AuthService;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignInCommand;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignUpCommand;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignInResult;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignUpResult;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.Role;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.SignInReq;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.SignUpReq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("회원가입 요청 성공")
    void signUpTest() throws Exception {
        SignUpReq req = new SignUpReq("hong", "password", "slack_hong", Role.HUB_DRIVER, UUID.randomUUID());
        SignUpResult mockResult = new SignUpResult(
            1L,
            "hong",
            "slackId",
            "HUB_DRIVER",
            "APPROVED",
            LocalDateTime.now()
        );
        Mockito.when(authService.signUp(any(SignUpCommand.class))).thenReturn(mockResult);

        mockMvc.perform(post("/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 요청 성공")
    void signInTest() throws Exception {
        SignInReq req = new SignInReq("hong", "password");
        SignInResult mockResult = new SignInResult("access-token", "refresh-token");

        Mockito.when(authService.signIn(any(SignInCommand.class))).thenReturn(mockResult);

        mockMvc.perform(post("/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").value("access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("토큰 재발급 요청 성공")
    void reissueTest() throws Exception {
        String refreshToken = "refresh-token";
        SignInResult mockResult = new SignInResult("new-access", "new-refresh");

        Mockito.when(authService.reissue(refreshToken)).thenReturn(mockResult);

        mockMvc.perform(post("/v1/auth/reissue")
                .header("Refresh-Token", refreshToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").value("new-access"))
            .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"));
    }

    @Test
    @DisplayName("로그아웃 요청 성공")
    void logoutTest() throws Exception {
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        mockMvc.perform(post("/v1/auth/logout")
                .header("Authorization", accessToken)
                .header("Refresh-Token", refreshToken))
            .andExpect(status().isOk());
    }
}