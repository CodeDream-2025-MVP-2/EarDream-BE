package com.eardream.domain.subscription.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 구독 생성 요청 DTO (POST /subscriptions - 구독 시작)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCreateRequest {
    
    @NotNull(message = "가족 그룹 ID는 필수입니다")
    private Long familyId;
    
    @NotNull(message = "구독 요금은 필수입니다")
    @Positive(message = "구독 요금은 양수여야 합니다")
    private BigDecimal planPrice;
    
    private String inicisBillkey;
}