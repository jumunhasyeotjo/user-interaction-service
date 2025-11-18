package com.jumunhasyeotjo.userinteract.auth.application.service;

public interface RefreshTokenRepository {
    void save(String name, String refreshToken, long expirationMillis);

    Object findByName(String name);

    void remove(String name);

}
