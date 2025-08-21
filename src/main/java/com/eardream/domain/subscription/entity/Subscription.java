package com.eardream.domain.subscription.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 구독 정보 엔티티 (schema.sql의 subscriptions 테이블과 매칭)
 * - 가족 그룹의 구독 상태 및 이니시스 결제 정보 관리
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    /**
     * 구독 내부 고유 ID (Primary Key, 자동증가)
     */
    private Long id;
    
    /**
     * 구독 가족 그룹 ID (Foreign Key)
     */
    private Long familyId;
    
    /**
     * 구독 월 요금 (원, 정수)
     */
    private BigDecimal planPrice;
    
    /**
     * 구독 상태 
     * - ACTIVE: 활성
     * - CANCELLED: 해지 
     * - PAUSED: 일시중지
     */
    private SubscriptionStatus status;
    
    /**
     * 다음 결제 예정일
     */
    private LocalDate nextBillingDate;
    
    /**
     * 이니시스 빌링키 (정기결제용)
     */
    private String inicisBillkey;
    
    /**
     * 구독 시작일시
     */
    private LocalDateTime startedAt;
    
    /**
     * 구독 해지일시
     */
    private LocalDateTime cancelledAt;
    
    /**
     * 구독 일시정지 시작일시
     */
    private LocalDateTime pauseStartedAt;
    
    /**
     * 구독 일시정지 종료일시
     */
    private LocalDateTime pauseEndedAt;
    
    /**
     * 구독 상태 열거형
     */
    public enum SubscriptionStatus {
        ACTIVE("활성"),
        CANCELLED("해지"),
        PAUSED("일시중지");
        
        private final String description;
        
        SubscriptionStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}