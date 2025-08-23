package com.eardream.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 서버에서 정기결제 실행 시 사용하는 DTO
 * 스케줄러나 관리자 API에서 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPaymentRequestDto {
    
    @NotBlank(message = "가족 ID는 필수입니다")
    private String familyId;
    
    @NotNull(message = "결제 금액은 필수입니다")
    @Positive(message = "결제 금액은 0보다 커야 합니다")
    private BigDecimal amount;
    
    private String orderName;  // 주문명 (ex: "2024년 12월 이어드림 구독")
    
    private String customerUid;  // 빌링키 조회용 고객 UID
}