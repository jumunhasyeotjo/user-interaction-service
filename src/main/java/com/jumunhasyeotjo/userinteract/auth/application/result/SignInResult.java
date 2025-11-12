package com.jumunhasyeotjo.userinteract.auth.application.result;

public record SignInResult(
    String accessToken,
    String refreshToken
) {
}
