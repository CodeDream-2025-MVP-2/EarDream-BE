package com.eardream.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 정보 엔티티
 * 포트원 V2 SDK와 연동되는 결제 데이터 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    /** 결제 ID (Primary Key) */
    private String paymentId;
    
    /** 주문 ID (Foreign Key) */
    private String orderId;
    
    /** 사용자 ID (Foreign Key) */
    private String userId;
    
    /** 가족 그룹 ID (Foreign Key) */
    private String familyId;
    
    /** 포트원 거래 ID */
    private String portoneTransactionId;
    
    /** 빌링키 ID (정기결제용) */
    private String billingKeyId;
    
    /** 영수증 URL */
    private String receiptUrl;
    
    /** 결제 금액 */
    private BigDecimal amount;
    
    /** 통화 코드 (기본: KRW) */
    private String currency;
    
    /** 결제 방법 (CARD, BANK, KAKAOPAY 등) */
    private String paymentMethod;
    
    /** 결제 상태 */
    private PaymentStatus status;
    
    /** 결제 타입 */
    private PaymentType type;
    
    /** PG사 코드 */
    private String pgProvider;
    
    /** PG사 거래 ID */
    private String pgTransactionId;
    
    /** 승인 번호 */
    private String approvalNumber;
    
    /** 실패 코드 */
    private String failureCode;
    
    /** 실패 메시지 */
    private String failureMessage;
    
    /** 카드 정보 (마스킹) */
    private String cardInfo;
    
    /** 구매자 이름 */
    private String buyerName;
    
    /** 구매자 이메일 */
    private String buyerEmail;
    
    /** 구매자 전화번호 */
    private String buyerPhone;
    
    /** 상품명 */
    private String productName;
    
    /** 요청 IP */
    private String requestIp;
    
    /** 요청 일시 */
    private LocalDateTime requestedAt;
    
    /** 승인 일시 */
    private LocalDateTime approvedAt;
    
    /** 실패 일시 */
    private LocalDateTime failedAt;
    
    /** 취소 일시 */
    private LocalDateTime cancelledAt;
    
    /** 생성일시 */
    private LocalDateTime createdAt;
    
    /** 수정일시 */
    private LocalDateTime updatedAt;
    
    /**
     * 결제 상태 enum
     */
    public enum PaymentStatus {
        PENDING,        // 결제 대기
        APPROVED,       // 결제 승인
        FAILED,         // 결제 실패
        CANCELLED,      // 결제 취소
        PARTIAL_CANCELLED, // 부분 취소
        REFUNDED        // 환불
    }
    
    /**
     * 결제 타입 enum
     */
    public enum PaymentType {
        ONETIME,        // 일회성 결제
        SUBSCRIPTION,   // 정기결제
        BILLING_KEY     // 빌링키 발급
    }
}