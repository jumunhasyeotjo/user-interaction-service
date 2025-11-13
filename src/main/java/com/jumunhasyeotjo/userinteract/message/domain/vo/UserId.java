package com.jumunhasyeotjo.userinteract.message.domain.vo;

import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserId {
    private Long userId;

    private UserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_USER);
        }
        this.userId = userId;
    }

    public static UserId of(Long userId) {
        return new UserId(userId);
    }
}
