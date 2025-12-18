package com.jumunhasyeotjo.userinteract.auth.presentation.dto.res;

import com.jumunhasyeotjo.userinteract.auth.application.result.PassportResult;

public record PassportRes(
    String passport
) {
    public static PassportRes fromPassportResult(PassportResult passportResult) {
        return new PassportRes(passportResult.passport());
    }
}
