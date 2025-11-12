package com.jumunhasyeotjo.userinteract.auth.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_KEY = "refresh:";

    public void save(String name, String refreshToken, long expirationMillis) {
        String key = REFRESH_TOKEN_KEY + name;
        redisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
    }

    public Object findByName(String name) {
        String key = REFRESH_TOKEN_KEY + name;
        return redisTemplate.opsForValue().get(key);
    }

    public void remove(String name) {
        String key = REFRESH_TOKEN_KEY + name;
        redisTemplate.delete(key);
    }

}
