package com.jumunhasyeotjo.userinteract.auth.application.service;

public interface BlacklistRepository {
    void save(String accessToken, long expirationMillis);

    boolean isBlacklisted(String accessToken);

    void delete(String accessToken);
}
