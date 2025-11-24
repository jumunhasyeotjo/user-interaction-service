package com.jumunhasyeotjo.userinteract.user.presentation.dto.req;

import java.util.UUID;

public record BelongReq(
    Long userId,
    UUID hubId
) {
}
