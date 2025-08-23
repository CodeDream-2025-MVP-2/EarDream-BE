package com.eardream.domain.payment.dto;

import com.eardream.domain.payment.entity.BillingKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 빌링키 응답 DTO
 * 포트원 V2 SDK IssueBillingKeyResponse 구조 기반
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingKeyResponseDto {
    
    /** 빌링키 ID */
    private String billingKeyId;
    
    /** 포트원 빌링키 */
    private String portoneKey;
    
    /** 거래 타입 (항상 "ISSUE_BILLING_KEY") */
    private String transactionType;
    
    /** PG사 코드 */
    private String pgProvider;
    
    /** 결제 수단 정보 (마스킹) */
    private String paymentMethodInfo;
    
    /** 카드 타입 */
    private String cardType;
    
    /** 카드사 코드 */
    private String cardCompany;
    
    /** 상태 */
    private BillingKey.BillingKeyStatus status;
    
    /** 발급 일시 */
    private LocalDateTime issuedAt;
    
    /** 만료 일시 */
    private LocalDateTime expiredAt;
    
    /** 에러 코드 (실패 시) */
    private String errorCode;
    
    /** 에러 메시지 (실패 시) */
    private String errorMessage;
    
    /** PG사 에러 코드 (실패 시) */
    private String pgErrorCode;
    
    /** PG사 에러 메시지 (실패 시, 사용자에게 표시 권장) */
    private String pgErrorMessage;
    
    /**
     * BillingKey 엔티티로부터 DTO 생성
     */
    public static BillingKeyResponseDto fromEntity(BillingKey billingKey) {
        return BillingKeyResponseDto.builder()
                .billingKeyId(billingKey.getBillingKeyId())
                .portoneKey(billingKey.getPortoneKey())
                .transactionType("ISSUE_BILLING_KEY")
                .pgProvider(billingKey.getPgProvider())
                .paymentMethodInfo(billingKey.getPaymentMethodInfo())
                .cardType(billingKey.getCardType())
                .cardCompany(billingKey.getCardCompany())
                .status(billingKey.getStatus())
                .issuedAt(billingKey.getIssuedAt())
                .expiredAt(billingKey.getExpiredAt())
                .build();
    }
    
    /**
     * 실패 응답 생성
     */
    public static BillingKeyResponseDto failure(String errorCode, String errorMessage, 
                                               String pgErrorCode, String pgErrorMessage) {
        return BillingKeyResponseDto.builder()
                .transactionType("ISSUE_BILLING_KEY")
                .status(BillingKey.BillingKeyStatus.INACTIVE)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .pgErrorCode(pgErrorCode)
                .pgErrorMessage(pgErrorMessage)
                .build();
    }
}