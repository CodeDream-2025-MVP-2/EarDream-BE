package com.eardream.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

/**
 * 결제 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    
    /** 주문 ID */
    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;
    
    /** 결제 금액 */
    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    private BigDecimal amount;
    
    /** 상품명 */
    @NotBlank(message = "상품명은 필수입니다.")
    private String productName;
    
    /** 결제 방법 */
    private String paymentMethod;
    
    /** 통화 코드 (기본: KRW) */
    @Value("KRW")
    private String currency;
    
    /** 구매자 이름 */
    private String buyerName;
    
    /** 구매자 이메일 */
    private String buyerEmail;
    
    /** 구매자 전화번호 */
    private String buyerPhone;
    
    /** 빌링키 (정기결제 시) */
    private String billingKey;
    
    /** 고객사 주문 ID */
    private String merchantOrderId;
    
    /** 결제 성공 시 리다이렉트 URL */
    private String successUrl;
    
    /** 결제 실패 시 리다이렉트 URL */
    private String failUrl;
    
    /** 결제 취소 시 리다이렉트 URL */
    private String cancelUrl;
    
    /** 결제 알림 수신 URL (Webhook) */
    private String notificationUrl;
    
    /** 추가 정보 */
    private String customData;
}