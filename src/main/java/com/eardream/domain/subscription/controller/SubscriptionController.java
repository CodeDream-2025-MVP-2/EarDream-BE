package com.eardream.domain.subscription.controller;

import com.eardream.domain.subscription.dto.SubscriptionCreateRequest;
import com.eardream.domain.subscription.dto.SubscriptionResponse;
import com.eardream.domain.subscription.dto.SubscriptionUpdateRequest;
import com.eardream.domain.subscription.service.SubscriptionService;
import com.eardream.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 구독 관리 컨트롤러 (API 명세서 기반)
 */
@Slf4j
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    /**
     * 구독 시작 (POST /subscriptions)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(
            @Valid @RequestBody SubscriptionCreateRequest request) {
        
        log.info("구독 시작 요청 - familyId: {}, planPrice: {}", 
                request.getFamilyId(), request.getPlanPrice());
        
        SubscriptionResponse response = subscriptionService.createSubscription(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "구독이 성공적으로 시작되었습니다."));
    }
    
    /**
     * 내 구독 현황 조회 (GET /subscriptions/me)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @RequestParam Long familyId) {
        
        log.debug("내 구독 현황 조회 요청 - familyId: {}", familyId);
        
        SubscriptionResponse response = subscriptionService.getMySubscription(familyId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 구독 변경 (PATCH /subscriptions/{id})
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionUpdateRequest request) {
        
        log.info("구독 변경 요청 - id: {}", id);
        
        SubscriptionResponse response = subscriptionService.updateSubscription(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "구독 정보가 성공적으로 변경되었습니다."));
    }
    
    /**
     * 구독 취소 (DELETE /subscriptions/{id})
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @PathVariable Long id) {
        
        log.info("구독 취소 요청 - id: {}", id);
        
        subscriptionService.cancelSubscription(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "구독이 성공적으로 취소되었습니다."));
    }
}