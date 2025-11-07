package com.jumunhasyeotjo.userinteract.common.error;

import com.jumunhasyeotjo.userinteract.common.ApiRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.jumunhasyeotjo.userinteract.common.error.ErrorCode.INVALID_JSON;
import static com.jumunhasyeotjo.userinteract.common.error.ErrorCode.VALIDATION_FAILED;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * JSON 파싱 에러 처리
     * 예시: {"name": "John",} (마지막 콤마로 인한 JSON 문법 오류)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiRes<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("JSON parsing error: {}", ex.getMessage());
        return ResponseEntity
            .badRequest()
            .body(ApiRes.error(INVALID_JSON.getCode(), INVALID_JSON.getMessage()));
    }

    /**
     * @Valid 유효성 검사 실패 처리
     * 예시: @Email String email -> "invalid" (이메일 형식 불일치)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiRes<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);

        return ResponseEntity
            .badRequest()
            .body(ApiRes.error(VALIDATION_FAILED.getCode(), VALIDATION_FAILED.getMessage()+" "+ errors));
    }

    /**
     * 비즈니스 로직 예외 처리
     * 예시: throw new BusinessException(ErrorCode.USER_NOT_FOUND)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiRes<?>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.error("BusinessException: {}", ex.getMessage());
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiRes.error(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * 예상하지 못한 모든 예외 처리
     * 예시: NullPointerException, DB 연결 오류 등
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiRes<?>> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiRes.error(errorCode.getCode(),
                errorCode.getMessage()));
    }
}
