package com.eardream.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * PortOne 결제 검증 결과 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResult {
    
    private boolean valid;  // 검증 성공 여부
    
    private String impUid;  // PortOne 거래 고유번호
    
    private String merchantUid;  // 상점 거래 ID
    
    private BigDecimal amount;  // 결제 금액
    
    private String payMethod;  // 결제 수단
    
    private String buyerName;  // 구매자 이름
    
    private String buyerEmail;  // 구매자 이메일
    
    private String buyerTel;  // 구매자 연락처
    
    private String name;  // 상품명
    
    private String status;  // 결제 상태
    
    private String failReason;  // 실패 사유
}