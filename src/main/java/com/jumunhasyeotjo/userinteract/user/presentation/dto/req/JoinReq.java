package com.jumunhasyeotjo.userinteract.user.presentation.dto.req;

import lombok.NonNull;

import java.util.UUID;

public record JoinReq(
    String name,
    String password,
    String slackId,
    String role,
    UUID belong
) {

}
