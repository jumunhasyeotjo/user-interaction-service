package com.jumunhasyeotjo.userinteract.auth.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "blacklist:";

    public void save(String accessToken, long expirationMillis) {
        redisTemplate.opsForValue().set(PREFIX + accessToken, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(PREFIX + accessToken);
    }

    public void delete(String accessToken) {
        redisTemplate.delete(PREFIX + accessToken);
    }
}
