package com.jumunhasyeotjo.userinteract.auth.application;

import com.jumunhasyeotjo.userinteract.auth.application.result.PassportResult;
import com.jumunhasyeotjo.userinteract.auth.application.service.JwtProvider;
import com.library.passport.HmacUtil;
import com.library.passport.proto.PassportProto.Passport;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassportService {

    private final HmacUtil hmacUtil;

    private final JwtProvider jwtProvider;

    @Value("${eas.passport.expiration}")
    private long EXPIRE_SECONDS;

    /**
     * Passport 발급
     */
    public PassportResult issuePassport(String jwt) {
        // 1) JWT 파싱
        Claims claims = jwtProvider.extractClaims(jwt);
        String name = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        String belong = claims.get("belong", String.class);

        // 3) Passport 발급 (HMAC+Protobuf)
        String passport = generatePassport(userId, name, role, belong);

        // 4) JSON 응답
        return PassportResult.convertToIssuePassportResponse(passport);
    }

    private String generatePassport(Long userId, String name, String role, String belong) {
        long now = Instant.now().getEpochSecond();
        long expire = now + EXPIRE_SECONDS;

        // Protobuf 객체 생성, protobuf는 기본적으로 null을 허용하지 않는다. 하지만 입력하지 않을 시 default 값이 들어감.
        Passport passport;
        if (belong != null) {
            passport = Passport.newBuilder()
                .setPassportId(UUID.randomUUID().toString())
                .setUserId(userId)
                .setName(name)
                .setRole(role)
                .setBelong(belong)
                .setIssuedAt(now)
                .setExpiresAt(expire)
                .build();
        } else {
            passport = Passport.newBuilder()
                .setPassportId(UUID.randomUUID().toString())
                .setUserId(userId)
                .setName(name)
                .setRole(role)
                .setIssuedAt(now)
                .setExpiresAt(expire)
                .build();
        }

        // 직렬화 -> byte[]
        byte[] rawBytes = passport.toByteArray();

        // HMAC 서명 + Base64
        return hmacUtil.signPassport(rawBytes);
    }
}
