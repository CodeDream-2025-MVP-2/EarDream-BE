package com.eardream.domain.payment.dto;

import com.eardream.domain.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    
    /** 결제 ID */
    private String paymentId;
    
    /** 주문 ID */
    private String orderId;
    
    /** 포트원 거래 ID */
    private String portoneTransactionId;
    
    /** 결제 금액 */
    private BigDecimal amount;
    
    /** 통화 코드 */
    private String currency;
    
    /** 결제 방법 */
    private String paymentMethod;
    
    /** 결제 상태 */
    private Payment.PaymentStatus status;
    
    /** 결제 타입 */
    private Payment.PaymentType type;
    
    /** PG사 코드 */
    private String pgProvider;
    
    /** 승인 번호 */
    private String approvalNumber;
    
    /** 실패 코드 */
    private String failureCode;
    
    /** 실패 메시지 */
    private String failureMessage;
    
    /** 카드 정보 (마스킹) */
    private String cardInfo;
    
    /** 상품명 */
    private String productName;
    
    /** 승인 일시 */
    private LocalDateTime approvedAt;
    
    /** 생성일시 */
    private LocalDateTime createdAt;
    
    /**
     * Payment 엔티티로부터 DTO 생성
     */
    public static PaymentResponseDto fromEntity(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .portoneTransactionId(payment.getPortoneTransactionId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .type(payment.getType())
                .pgProvider(payment.getPgProvider())
                .approvalNumber(payment.getApprovalNumber())
                .failureCode(payment.getFailureCode())
                .failureMessage(payment.getFailureMessage())
                .cardInfo(payment.getCardInfo())
                .productName(payment.getProductName())
                .approvedAt(payment.getApprovedAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}