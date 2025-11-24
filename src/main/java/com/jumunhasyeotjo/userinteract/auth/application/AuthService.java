package com.jumunhasyeotjo.userinteract.auth.application;

import com.jumunhasyeotjo.userinteract.auth.application.command.SignInCommand;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignUpCommand;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignInResult;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignUpResult;
import com.jumunhasyeotjo.userinteract.auth.application.service.*;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.JoinReq;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.TokenDto;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDto;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.Role;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final UserClient userClient;
    private final HubClient hubClient;
    private final CompanyClient companyClient;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistRepository blacklistRepository;


    public SignUpResult signUp(SignUpCommand command) {
        String password = passwordEncoder.encode(command.password());
        Role role = command.role();
        UUID belong = command.belong();

        if (role.equals(Role.HUB_MANAGER) || role.equals(Role.COMPANY_DRIVER)) {
            if (!hubClient.exist(command.belong())) {
                throw new BusinessException(ErrorCode.INVALID_HUB);
            }
        } else if (role.equals(Role.COMPANY_MANAGER)) {
            if (!companyClient.exist(command.belong())) {
                throw new BusinessException(ErrorCode.INVALID_COMPANY);
            }
        }

        JoinReq req = new JoinReq(
            command.name(),
            password,
            command.slackId(),
            role.name(),
            belong
        );

        return SignUpResult.from(userClient.join(req));
    }

    public SignInResult signIn(SignInCommand command) {
        UserDto dto = userClient.validate(command.name(), command.password());
        if (dto == null) {
            throw new BusinessException(ErrorCode.INVALID_USERINFO);
        }

        TokenDto token = jwtProvider.generateToken(dto.userId(), dto.name(), dto.role());

        refreshTokenRepository.save(dto.name(), token.refreshToken(), jwtProvider.getRefreshExpiration());

        return new SignInResult(token.accessToken(), token.refreshToken());
    }

    public SignInResult reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Claims claims = jwtProvider.extractClaims(refreshToken);
        String name = claims.getSubject();

        String savedToken = (String) refreshTokenRepository.findByName(name);
        if (!refreshToken.equals(savedToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        TokenDto newToken = jwtProvider.generateToken(claims.get("userId", Long.class), name, claims.get("role", String.class));

        refreshTokenRepository.save(name, newToken.refreshToken(), jwtProvider.getRefreshExpiration());

        return new SignInResult(newToken.accessToken(), newToken.refreshToken());
    }

    public void logout(String accessToken, String refreshToken) {
        String name = jwtProvider.getSubjectFromToken(refreshToken);
        refreshTokenRepository.remove(name);

        long expirationMillis = jwtProvider.getRemainingExpiration(accessToken);
        if (expirationMillis > 0) {
            blacklistRepository.save(accessToken, expirationMillis);
        }
    }
}
