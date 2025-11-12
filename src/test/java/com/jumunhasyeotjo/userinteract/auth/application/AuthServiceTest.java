package com.jumunhasyeotjo.userinteract.auth.application;

import com.jumunhasyeotjo.userinteract.auth.application.command.SignInCommand;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignUpCommand;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignInResult;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignUpResult;
import com.jumunhasyeotjo.userinteract.auth.application.service.CompanyClient;
import com.jumunhasyeotjo.userinteract.auth.application.service.HubClient;
import com.jumunhasyeotjo.userinteract.auth.application.service.JwtProvider;
import com.jumunhasyeotjo.userinteract.auth.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.TokenDto;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDto;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.repository.RefreshTokenRedisRepository;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.repository.TokenBlacklistRepository;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.Role;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;

import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private JwtProvider jwtProvider;
    private UserClient userClient;
    private HubClient hubClient;
    private CompanyClient companyClient;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenRedisRepository refreshTokenRedisRepository;
    private TokenBlacklistRepository tokenBlacklistRepository;

    @BeforeEach
    void setUp() {
        jwtProvider = mock(JwtProvider.class);
        userClient = mock(UserClient.class);
        hubClient = mock(HubClient.class);
        companyClient = mock(CompanyClient.class);
        passwordEncoder = mock(PasswordEncoder.class);
        refreshTokenRedisRepository = mock(RefreshTokenRedisRepository.class);
        tokenBlacklistRepository = mock(TokenBlacklistRepository.class);

        authService = new AuthService(
            jwtProvider,
            userClient,
            hubClient,
            companyClient,
            passwordEncoder,
            refreshTokenRedisRepository,
            tokenBlacklistRepository
        );
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUpWillSuccess() {
        SignUpCommand command = new SignUpCommand("hong", "pw", "slackId", Role.HUB_MANAGER, UUID.randomUUID());

        when(passwordEncoder.encode("pw")).thenReturn("encodedPw");
        when(hubClient.exist(command.belong())).thenReturn(true);
        when(userClient.join(ArgumentMatchers.any())).thenReturn(
            new UserDto(
                1L,
                "hong",
                "slackId",
                "HUB_DRIVER",
                "PENDING",
                LocalDateTime.now()
            )
        );

        SignUpResult result = authService.signUp(command);

        assertThat(result).isNotNull();
        verify(userClient).join(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("회원가입 - 허브 존재하지 않으면 예외")
    void signUpWithInvalidHubWillThrowException() {
        SignUpCommand command = new SignUpCommand("hong", "pw", "slackId", Role.HUB_MANAGER, UUID.randomUUID());

        when(hubClient.exist(command.belong())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.signUp(command));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_HUB);
    }

    @Test
    @DisplayName("로그인 성공")
    void signInWillSuccess() {
        SignInCommand command = new SignInCommand("hong", "pw");
        UserDto userDto = new UserDto(
            1L,
            "hong",
            "slackId",
            "HUB_DRIVER",
            "PENDING",
            LocalDateTime.now()
        );
        TokenDto tokenDto = TokenDto.of("access", "refresh");

        when(userClient.validate("hong", "pw")).thenReturn(userDto);
        when(jwtProvider.generateToken(eq("hong"), anyString())).thenReturn(tokenDto);

        SignInResult result = authService.signIn(command);

        assertThat(result.accessToken()).isEqualTo("access");
        assertThat(result.refreshToken()).isEqualTo("refresh");
        verify(refreshTokenRedisRepository).save("hong", "refresh", jwtProvider.getRefreshExpiration());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissueWillSuccess() {
        String refreshToken = "refresh";
        Claims claims = mock(Claims.class);

        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.extractClaims(refreshToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("hong");
        when(claims.get("role", String.class)).thenReturn("MASTER");
        when(refreshTokenRedisRepository.findByName("hong")).thenReturn("refresh");
        when(jwtProvider.generateToken("hong", "MASTER")).thenReturn(TokenDto.of("newAccess", "newRefresh"));

        SignInResult result = authService.reissue(refreshToken);

        assertThat(result.accessToken()).isEqualTo("newAccess");
        assertThat(result.refreshToken()).isEqualTo("newRefresh");
        verify(refreshTokenRedisRepository).save("hong", "newRefresh", jwtProvider.getRefreshExpiration());
    }

    @Test
    @DisplayName("로그아웃 시 토큰 삭제 및 블랙리스트 저장")
    void logoutTestWillSuccess() {
        String accessToken = "access";
        String refreshToken = "refresh";
        Claims claims = mock(Claims.class);

        when(jwtProvider.getSubjectFromToken(refreshToken)).thenReturn("hong");
        when(jwtProvider.getRemainingExpiration(accessToken)).thenReturn(3600000L);

        authService.logout(accessToken, refreshToken);

        verify(refreshTokenRedisRepository).remove("hong");
        verify(tokenBlacklistRepository).save(accessToken, 3600000L);
    }
}
