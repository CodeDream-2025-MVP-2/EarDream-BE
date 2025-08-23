package com.eardream.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전체 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 공통 에러
    INTERNAL_SERVER_ERROR("COMMON_001", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT_VALUE("COMMON_002", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("COMMON_003", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    ENTITY_NOT_FOUND("COMMON_004", "엔티티를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_TYPE_VALUE("COMMON_005", "잘못된 타입 값입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("COMMON_006", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    
    // 인증/인가 에러
    UNAUTHORIZED("AUTH_001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AUTH_002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AUTH_003", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("AUTH_004", "토큰을 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    
    // 사용자 에러
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_002", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    USER_ACCESS_DENIED("USER_003", "사용자 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    
    // 가족 그룹 에러
    FAMILY_NOT_FOUND("FAMILY_001", "가족 그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FAMILY_ACCESS_DENIED("FAMILY_002", "가족 그룹 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FAMILY_MEMBER_LIMIT_EXCEEDED("FAMILY_003", "가족 구성원 수가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INVITE_CODE("FAMILY_004", "유효하지 않은 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    
    // 소식 에러
    POST_NOT_FOUND("POST_001", "소식을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    POST_ACCESS_DENIED("POST_002", "소식 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    MONTHLY_POST_LIMIT_EXCEEDED("POST_003", "월 소식 작성 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_POST_CONTENT("POST_004", "잘못된 소식 내용입니다.", HttpStatus.BAD_REQUEST),
    
    // 파일 업로드 에러
    FILE_UPLOAD_FAILED("FILE_001", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE("FILE_002", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("FILE_003", "파일 크기가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND("FILE_004", "파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // 결제 에러
    PAYMENT_NOT_FOUND("PAYMENT_001", "결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PAYMENT_ACCESS_DENIED("PAYMENT_002", "결제 정보 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    PAYMENT_PROCESS_FAILED("PAYMENT_003", "결제 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_CANCEL_FAILED("PAYMENT_004", "결제 취소에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_CANCEL_NOT_ALLOWED("PAYMENT_005", "취소할 수 없는 결제입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_VERIFICATION_FAILED("PAYMENT_006", "결제 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    
    // 빌링키 에러
    BILLING_KEY_NOT_FOUND("BILLING_001", "빌링키를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BILLING_KEY_ACCESS_DENIED("BILLING_002", "빌링키 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    BILLING_KEY_ISSUE_FAILED("BILLING_003", "빌링키 발급에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BILLING_KEY_INACTIVE("BILLING_004", "비활성화된 빌링키입니다.", HttpStatus.BAD_REQUEST),
    BILLING_KEY_REGISTER_FAILED("BILLING_005", "빌링키 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BILLING_KEY_ALREADY_EXISTS("BILLING_006", "이미 등록된 빌링키입니다.", HttpStatus.CONFLICT),
    SUBSCRIPTION_PAYMENT_FAILED("BILLING_007", "정기결제 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 포트원 연동 에러
    PORTONE_TOKEN_FAILED("PORTONE_001", "포트원 토큰 발급에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PORTONE_PAYMENT_REQUEST_FAILED("PORTONE_002", "포트원 결제 요청에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PORTONE_BILLING_KEY_FAILED("PORTONE_003", "포트원 빌링키 발급에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PORTONE_BILLING_PAYMENT_FAILED("PORTONE_004", "포트원 빌링키 결제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PORTONE_CANCEL_FAILED("PORTONE_005", "포트원 결제 취소에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PORTONE_BILLING_KEY_DELETE_FAILED("PORTONE_006", "포트원 빌링키 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 웹훅 에러
    WEBHOOK_PROCESS_FAILED("WEBHOOK_001", "웹훅 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WEBHOOK_SIGNATURE_INVALID("WEBHOOK_002", "웹훅 서명 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    
    // 구독 에러
    SUBSCRIPTION_NOT_FOUND("SUBSCRIPTION_001", "구독 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_ACCESS_DENIED("SUBSCRIPTION_002", "구독 정보 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    SUBSCRIPTION_ALREADY_EXISTS("SUBSCRIPTION_003", "이미 존재하는 구독입니다.", HttpStatus.CONFLICT),
    SUBSCRIPTION_CANCEL_FAILED("SUBSCRIPTION_004", "구독 취소에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}