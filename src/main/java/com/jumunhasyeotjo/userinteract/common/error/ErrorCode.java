package com.jumunhasyeotjo.userinteract.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "서버 에러가 발생했습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E002", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E003", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E004", "접근이 거부되었습니다."),
    CREATE_VALIDATE_EXCEPTION(HttpStatus.BAD_REQUEST,"E005", "객체 생성에 실패했습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "E006", "입력값 검증에 실패했습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "E007",  "잘못된 JSON 형식입니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "E008", "유효한 역할이 아닙니다."),
    STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "E009", "유효한 상태가 아닙니다.."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E010", "사용자를 찾을 수 없습니다."),
    TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "E011", "유효한 운전자 타입이 아닙니다."),
    ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "E012", "이미 승인된 유저입니다.")

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
