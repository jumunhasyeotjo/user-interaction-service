package com.jumunhasyeotjo.userinteract.auth.infrastructure;

import com.jumunhasyeotjo.userinteract.auth.application.service.JwtProvider;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.TokenDto;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtProviderImpl implements JwtProvider {
    @Value("${service.jwt.secret-key}")
    private String secret;

    private static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000L; // 1시간
    public static final long REFRESH_TOKEN_EXPIRATION = 30 * 24 * 60 * 60 * 1000L; // 30일

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public TokenDto generateToken(Long userId, String name, String role, UUID belong) {
        String accessToken = createAccessToken(userId, name, role, belong);
        String refreshToken = createRefreshToken(name);

        return TokenDto.of(accessToken, refreshToken);
    }

    public String createAccessToken(Long userId, String name, String role, UUID belong) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(name)
                .claim("userId", userId)
                .claim("role", role)
                .claim("belong", belong)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRATION))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String createRefreshToken(String name) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(name)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRATION))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    @Override
    public String removePrefix(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    @Override
    public Claims extractClaims(String token) {
        token = removePrefix(token);
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    @Override
    public String getSubjectFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        token = removePrefix(token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | io.jsonwebtoken.security.SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    @Override
    public Long getRemainingExpiration(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            long now = System.currentTimeMillis();
            return expiration.getTime() - now;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long getRefreshExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }
}
