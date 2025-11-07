package com.jumunhasyeotjo.userinteract.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) //null 값일 경우 json 표현 안함
public class ApiRes<T> {

    private final String code;
    private final String message;
    private final T data;

    private ApiRes(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 성공 응답
    public static <T> ApiRes<T> success(T data) {
        return new ApiRes<>(null, null, data);
    }

    // 실패 응답
    public static <T> ApiRes<T> error(String code, String message) {
        return new ApiRes<>(code, message, null);
    }
}
