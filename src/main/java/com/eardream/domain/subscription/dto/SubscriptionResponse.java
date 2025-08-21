package com.eardream.domain.subscription.dto;

import com.eardream.domain.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 구독 정보 응답 DTO (/subscriptions/me - 내 구독 현황 조회용)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    
    private Long id;
    
    private Long familyId;
    
    private String familyName;
    
    private BigDecimal planPrice;
    
    private Subscription.SubscriptionStatus status;
    
    private String statusDescription;
    
    private LocalDate nextBillingDate;
    
    private String inicisBillkey;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime cancelledAt;
    
    private LocalDateTime pauseStartedAt;
    
    private LocalDateTime pauseEndedAt;
    
    /**
     * Entity를 Response DTO로 변환
     */
    public static SubscriptionResponse from(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .familyId(subscription.getFamilyId())
                .planPrice(subscription.getPlanPrice())
                .status(subscription.getStatus())
                .statusDescription(subscription.getStatus().getDescription())
                .nextBillingDate(subscription.getNextBillingDate())
                .inicisBillkey(subscription.getInicisBillkey())
                .startedAt(subscription.getStartedAt())
                .cancelledAt(subscription.getCancelledAt())
                .pauseStartedAt(subscription.getPauseStartedAt())
                .pauseEndedAt(subscription.getPauseEndedAt())
                .build();
    }
}