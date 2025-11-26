package com.jumunhasyeotjo.userinteract.auth.application.service;

import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.TokenDto;
import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface JwtProvider {
    TokenDto generateToken(Long userId, String name, String role, UUID belong);
    String getSubjectFromToken(String token);
    String removePrefix(String token);
    Claims extractClaims(String token);
    boolean validateToken(String token);
    Long getRemainingExpiration(String token);
    Long getRefreshExpiration();
}
