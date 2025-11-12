package com.jumunhasyeotjo.userinteract.auth.application;

import com.jumunhasyeotjo.userinteract.auth.application.command.SignInCommand;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignUpCommand;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignInResult;
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

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserClient userClient;

    @Mock
    private HubClient hubClient;

    @Mock
    private CompanyClient companyClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_HubManager_ValidHub_CallsUserClientJoin() {
        // given
        SignUpCommand command = new SignUpCommand("홍길동", "pass123", "slackId",
            Role.HUB_MANAGER, UUID.randomUUID());

        when(passwordEncoder.encode(command.password())).thenReturn("encodedPass");
        when(hubClient.exist(command.belong())).thenReturn(true);

        // when
        authService.signUp(command);

        // then
        verify(userClient, times(1)).join(any());
    }

    @Test
    void signUp_HubManager_InvalidHub_ThrowsException() {
        SignUpCommand command = new SignUpCommand("홍길동", "pass123", "slackId",
            Role.HUB_MANAGER, UUID.randomUUID());

        when(passwordEncoder.encode(command.password())).thenReturn("encodedPass");
        when(hubClient.exist(command.belong())).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.signUp(command));
        verify(userClient, never()).join(any());
    }

    @Test
    void signIn_ValidUser_ReturnsTokens() {
        SignInCommand command = new SignInCommand("홍길동", "pass123");

        UserDto dto = new UserDto(1L, "홍길동", "slackId", Role.HUB_MANAGER.name(), "APPROVED", LocalDateTime.now());
        when(userClient.validate(command.name(), command.password())).thenReturn(dto);

        TokenDto tokenDto = TokenDto.of("accessToken", "refreshToken");
        when(jwtProvider.generateToken(dto.name(), dto.role())).thenReturn(tokenDto);
        when(jwtProvider.getRefreshExpiration()).thenReturn(1000L);

        SignInResult result = authService.signIn(command);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
        verify(refreshTokenRedisRepository).save(dto.name(), "refreshToken", 1000L);
    }

    @Test
    void signIn_InvalidUser_ThrowsException() {
        SignInCommand command = new SignInCommand("홍길동", "wrongPass");

        when(userClient.validate(command.name(), command.password())).thenReturn(null);

        assertThrows(BusinessException.class, () -> authService.signIn(command));
    }

    @Test
    void reissue_ValidRefreshToken_ReturnsNewTokens() {
        String refreshToken = "refreshToken";
        Claims claims = mock(Claims.class);

        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.extractClaims(refreshToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("홍길동");
        when(claims.get("role", String.class)).thenReturn(Role.HUB_MANAGER.name());

        when(refreshTokenRedisRepository.findByName("홍길동")).thenReturn(refreshToken);

        TokenDto newToken = TokenDto.of("newAccess", "newRefresh");
        when(jwtProvider.generateToken("홍길동", Role.HUB_MANAGER.name())).thenReturn(newToken);
        when(jwtProvider.getRefreshExpiration()).thenReturn(1000L);

        SignInResult result = authService.reissue(refreshToken);

        assertThat(result.accessToken()).isEqualTo("newAccess");
        assertThat(result.refreshToken()).isEqualTo("newRefresh");
        verify(refreshTokenRedisRepository).save("홍길동", "newRefresh", 1000L);
    }

    @Test
    void reissue_InvalidRefreshToken_ThrowsException() {
        String refreshToken = "invalidToken";
        when(jwtProvider.validateToken(refreshToken)).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.reissue(refreshToken));
    }

    @Test
    void logout_RemovesRefreshTokenAndAddsToBlacklist() {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        Claims refreshClaims = mock(Claims.class);
        Claims accessClaims = mock(Claims.class);

        when(jwtProvider.extractClaims(refreshToken)).thenReturn(refreshClaims);
        when(refreshClaims.getSubject()).thenReturn("홍길동");

        when(jwtProvider.getRemainingExpiration(accessToken)).thenReturn(1000L);

        authService.logout(accessToken, refreshToken);

        verify(refreshTokenRedisRepository).remove("홍길동");
        verify(tokenBlacklistRepository).save(accessToken, 1000L);
    }
}
