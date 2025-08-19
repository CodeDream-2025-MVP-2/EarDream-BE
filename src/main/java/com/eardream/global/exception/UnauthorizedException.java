package com.eardream.global.exception;

/**
 * 인증 실패 예외
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException() {
        super("인증이 필요합니다.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}