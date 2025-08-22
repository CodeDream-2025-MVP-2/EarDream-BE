package com.eardream.domain.subscription.service;

import com.eardream.domain.subscription.dto.SubscriptionCreateRequest;
import com.eardream.domain.subscription.dto.SubscriptionResponse;
import com.eardream.domain.subscription.dto.SubscriptionUpdateRequest;
import com.eardream.domain.subscription.entity.Subscription;
import com.eardream.domain.subscription.mapper.SubscriptionMapper;
import com.eardream.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 구독 관리 서비스 (API 명세서 기반)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {
    
    private final SubscriptionMapper subscriptionMapper;
    
    // 기본 월간 구독료 (원)
    private static final BigDecimal DEFAULT_PLAN_PRICE = new BigDecimal("29900");
    
    /**
     * 구독 생성 (POST /subscriptions)
     */
    @Transactional
    public SubscriptionResponse createSubscription(SubscriptionCreateRequest request) {
        log.info("구독 생성 시작 - familyId: {}, planPrice: {}", 
                request.getFamilyId(), request.getPlanPrice());
        
        // 기존 활성 구독 확인
        subscriptionMapper.findActiveByFamilyId(request.getFamilyId())
                .ifPresent(existing -> {
                    throw new BusinessException("이미 활성 구독이 존재합니다. familyId: " + request.getFamilyId());
                });
        
        // 구독 정보 생성
        Subscription subscription = Subscription.builder()
                .familyId(request.getFamilyId())
                .planPrice(DEFAULT_PLAN_PRICE)
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .nextBillingDate(calculateNextBillingDate())
                .inicisBillkey(request.getInicisBillkey())
                .startedAt(LocalDateTime.now())
                .build();
        
        int result = subscriptionMapper.insertSubscription(subscription);
        if (result == 0) {
            throw new BusinessException("구독 생성에 실패했습니다.");
        }
        
        log.info("구독 생성 완료 - subscriptionId: {}", subscription.getId());
        return SubscriptionResponse.from(subscription);
    }
    
    /**
     * 내 구독 현황 조회 (GET /subscriptions/me)
     */
    public SubscriptionResponse getMySubscription(Long familyId) {
        log.debug("내 구독 현황 조회 - familyId: {}", familyId);
        
        Subscription subscription = subscriptionMapper.findActiveByFamilyId(familyId)
                .orElseThrow(() -> new BusinessException("활성 구독 정보를 찾을 수 없습니다. familyId: " + familyId));
        
        return SubscriptionResponse.from(subscription);
    }

    /**
     * 구독 ID로 조회
     */
    public SubscriptionResponse getSubscription(Long id) {
        log.debug("구독 조회 - id: {}", id);
        
        Subscription subscription = subscriptionMapper.findById(id)
                .orElseThrow(() -> new BusinessException("구독 정보를 찾을 수 없습니다. id: " + id));
        
        return SubscriptionResponse.from(subscription);
    }
    
    /**
     * 구독 정보 수정 (PATCH /subscriptions/{id})
     */
    @Transactional
    public SubscriptionResponse updateSubscription(Long id, SubscriptionUpdateRequest request) {
        log.info("구독 수정 시작 - id: {}", id);
        
        Subscription subscription = subscriptionMapper.findById(id)
                .orElseThrow(() -> new BusinessException("구독 정보를 찾을 수 없습니다. id: " + id));
        
        // 수정할 필드 업데이트
        if (request.getStatus() != null) {
            subscription.setStatus(request.getStatus());
            
            // 상태 변경에 따른 일시 업데이트
            if (request.getStatus() == Subscription.SubscriptionStatus.CANCELLED) {
                subscription.setCancelledAt(LocalDateTime.now());
            } else if (request.getStatus() == Subscription.SubscriptionStatus.PAUSED) {
                subscription.setPauseStartedAt(LocalDateTime.now());
            } else if (request.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
                subscription.setPauseEndedAt(LocalDateTime.now());
            }
        }
        
        if (request.getPlanPrice() != null) {
            subscription.setPlanPrice(request.getPlanPrice());
        }
        
        if (request.getNextBillingDate() != null) {
            subscription.setNextBillingDate(request.getNextBillingDate());
        }
        
        if (request.getInicisBillkey() != null) {
            subscription.setInicisBillkey(request.getInicisBillkey());
        }
        
        int result = subscriptionMapper.updateSubscription(subscription);
        if (result == 0) {
            throw new BusinessException("구독 수정에 실패했습니다.");
        }
        
        log.info("구독 수정 완료 - id: {}", id);
        return SubscriptionResponse.from(subscription);
    }
    
    /**
     * 구독 취소 (DELETE /subscriptions/{id})
     */
    @Transactional
    public void cancelSubscription(Long id) {
        log.info("구독 취소 시작 - id: {}", id);
        
        Subscription subscription = subscriptionMapper.findById(id)
                .orElseThrow(() -> new BusinessException("구독 정보를 찾을 수 없습니다. id: " + id));
        
        if (subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            throw new BusinessException("활성 상태의 구독만 취소할 수 있습니다.");
        }
        
        // 구독 해지 처리
        int result = subscriptionMapper.cancelSubscription(id);
        if (result == 0) {
            throw new BusinessException("구독 취소에 실패했습니다.");
        }
        
        log.info("구독 취소 완료 - id: {}", id);
    }
    
    /**
     * 구독 일시정지
     */
    @Transactional
    public void pauseSubscription(Long id) {
        log.info("구독 일시정지 - id: {}", id);
        
        int result = subscriptionMapper.pauseSubscription(id);
        if (result == 0) {
            throw new BusinessException("구독 일시정지에 실패했습니다.");
        }
    }
    
    /**
     * 구독 재개
     */
    @Transactional
    public void resumeSubscription(Long id) {
        log.info("구독 재개 - id: {}", id);
        
        int result = subscriptionMapper.resumeSubscription(id);
        if (result == 0) {
            throw new BusinessException("구독 재개에 실패했습니다.");
        }
    }
    
    /**
     * 다음 결제일 계산 (월간 구독 기준)
     */
    private LocalDate calculateNextBillingDate() {
        return LocalDate.now().plusMonths(1);
    }
}