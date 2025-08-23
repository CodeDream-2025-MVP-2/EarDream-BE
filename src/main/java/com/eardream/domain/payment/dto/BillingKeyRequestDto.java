package com.eardream.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 빌링키 발급 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingKeyRequestDto {
    
    /** 고객사 빌링키 ID */
    @NotBlank(message = "빌링키 ID는 필수입니다.")
    private String billingKeyId;
    
    /** PG사 코드 */
    private String pgProvider;
    
    /** 구매자 이름 */
    private String buyerName;
    
    /** 구매자 이메일 */
    private String buyerEmail;
    
    /** 구매자 전화번호 */
    private String buyerPhone;
    
    /** 카드번호 */
    private String cardNumber;
    
    /** 유효기간 (MMYY) */
    private String expiryDate;
    
    /** 생년월일 또는 사업자번호 */
    private String birthOrBizRegNo;
    
    /** 카드 비밀번호 앞 2자리 */
    private String passwordTwoDigits;
    
    /** 빌링키 발급 성공 시 리다이렉트 URL */
    private String successUrl;
    
    /** 빌링키 발급 실패 시 리다이렉트 URL */
    private String failUrl;
    
    /** 알림 수신 URL (Webhook) */
    private String notificationUrl;
    
    /** 추가 정보 */
    private String customData;
}