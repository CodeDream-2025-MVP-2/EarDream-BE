package com.eardream.domain.subscription.dto;

import com.eardream.domain.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 구독 정보 수정 요청 DTO (PATCH /subscriptions/{id} - 구독 변경)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUpdateRequest {
    
    private Subscription.SubscriptionStatus status;
    
    private BigDecimal planPrice;
    
    private LocalDate nextBillingDate;
    
    private String inicisBillkey;
}