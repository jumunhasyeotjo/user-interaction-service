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
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "유효한 역할이 아닙니다."),
    STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "유효한 상태가 아닙니다.."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "사용자를 찾을 수 없습니다."),
    TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "U004", "유효한 운전자 타입이 아닙니다."),
    NOT_APPROVAL_STATUS(HttpStatus.BAD_REQUEST, "U005", "승인이 가능한 상태가 아닙니다."),
    NOT_APPROVAL_TARGET(HttpStatus.BAD_REQUEST, "U006", "승인이 가능한 역할이 아닙니다."),
    DUPLICATE_USER_NAME(HttpStatus.BAD_REQUEST, "U007", "유저 이름이 중복됩니다."),
    INVALID_USERINFO(HttpStatus.BAD_REQUEST, "A001", "유저 이름 또는 비밀번호가 유효하지 않습니다."),
    INVALID_HUB(HttpStatus.BAD_REQUEST, "A002", "허브가 유효하지 않습니다."),
    INVALID_COMPANY(HttpStatus.BAD_REQUEST, "A003", "업체가 유효하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "A004", "토큰이 유효하지 않습니다."),
    INVALID_USER(HttpStatus.BAD_REQUEST, "M001", "사용자 번호가 유효하지 않습니다."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "M002", "메세지의 컨텐츠가 유효하지 않습니다."),


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
