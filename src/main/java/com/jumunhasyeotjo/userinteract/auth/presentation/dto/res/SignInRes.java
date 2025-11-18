package com.jumunhasyeotjo.userinteract.auth.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.auth.application.result.SignInResult;

public record SignInRes(
    String accessToken,
    String refreshToken
) {
    public static SignInRes from(SignInResult res) {
        return new SignInRes(res.accessToken(), res.refreshToken());
    }
}
