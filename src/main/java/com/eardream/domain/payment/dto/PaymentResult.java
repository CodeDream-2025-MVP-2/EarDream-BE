package com.eardream.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * PortOne 결제 실행 결과 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {
    
    private boolean success;  // 결제 성공 여부
    
    private String impUid;  // PortOne 거래 고유번호
    
    private String merchantUid;  // 상점 거래 ID
    
    private BigDecimal amount;  // 결제 금액
    
    private String payMethod;  // 결제 수단
    
    private String cardName;  // 카드사명
    
    private String cardNumber;  // 마스킹된 카드번호
    
    private String status;  // 결제 상태
    
    private String failReason;  // 실패 사유
    
    private String receiptUrl;  // 영수증 URL
}