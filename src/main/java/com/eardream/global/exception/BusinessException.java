package com.eardream.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 예외 클래스
 * 도메인별 비즈니스 규칙 위반 시 발생
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }
}