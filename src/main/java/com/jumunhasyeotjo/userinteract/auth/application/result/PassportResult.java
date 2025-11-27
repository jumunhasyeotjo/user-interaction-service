package com.jumunhasyeotjo.userinteract.auth.application.result;

public record PassportResult(
    String passport
) {
    public static PassportResult convertToIssuePassportResponse(String passport) {
        return new PassportResult(passport);
    }
}
