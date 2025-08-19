package com.eardream.global.common;

/**
 * 에러 코드 상수 정의
 * 도메인별로 에러 코드를 관리
 */
public class ErrorCode {
    
    // 공통 에러 코드
    public static final String INVALID_INPUT = "INVALID_INPUT";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    
    // 사용자 관련 에러 코드
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String INVALID_USER_TYPE = "INVALID_USER_TYPE";
    public static final String USER_PROFILE_INCOMPLETE = "USER_PROFILE_INCOMPLETE";
    
    // 가족 관련 에러 코드
    public static final String FAMILY_NOT_FOUND = "FAMILY_NOT_FOUND";
    public static final String FAMILY_ALREADY_EXISTS = "FAMILY_ALREADY_EXISTS";
    public static final String FAMILY_MEMBER_LIMIT_EXCEEDED = "FAMILY_MEMBER_LIMIT_EXCEEDED";
    public static final String INVALID_INVITE_CODE = "INVALID_INVITE_CODE";
    public static final String ALREADY_IN_FAMILY = "ALREADY_IN_FAMILY";
    public static final String NOT_FAMILY_LEADER = "NOT_FAMILY_LEADER";
    
    // 소식 관련 에러 코드
    public static final String POST_NOT_FOUND = "POST_NOT_FOUND";
    public static final String MONTHLY_POST_LIMIT_EXCEEDED = "MONTHLY_POST_LIMIT_EXCEEDED";
    public static final String IMAGE_UPLOAD_FAILED = "IMAGE_UPLOAD_FAILED";
    public static final String IMAGE_LIMIT_EXCEEDED = "IMAGE_LIMIT_EXCEEDED";
    public static final String CONTENT_TOO_LONG = "CONTENT_TOO_LONG";
    
    // 구독 관련 에러 코드
    public static final String SUBSCRIPTION_NOT_FOUND = "SUBSCRIPTION_NOT_FOUND";
    public static final String SUBSCRIPTION_ALREADY_EXISTS = "SUBSCRIPTION_ALREADY_EXISTS";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String PAYMENT_METHOD_NOT_FOUND = "PAYMENT_METHOD_NOT_FOUND";
    public static final String SUBSCRIPTION_EXPIRED = "SUBSCRIPTION_EXPIRED";
    
    // 출판물 관련 에러 코드
    public static final String PUBLICATION_NOT_FOUND = "PUBLICATION_NOT_FOUND";
    public static final String PUBLICATION_NOT_READY = "PUBLICATION_NOT_READY";
    public static final String PDF_GENERATION_FAILED = "PDF_GENERATION_FAILED";
    
    // 파일 관련 에러 코드
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    public static final String FILE_SIZE_EXCEEDED = "FILE_SIZE_EXCEEDED";
    public static final String INVALID_FILE_TYPE = "INVALID_FILE_TYPE";
    public static final String FILE_UPLOAD_FAILED = "FILE_UPLOAD_FAILED";
    
    private ErrorCode() {
        // 인스턴스 생성 방지
    }
}