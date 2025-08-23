package com.eardream.domain.payment.controller;

import com.eardream.domain.payment.dto.*;
import com.eardream.domain.payment.service.PaymentService;
import com.eardream.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 결제 관련 API 컨트롤러
 * 포트원 V2 SDK와 연동되는 결제 기능 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 관리 API")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 완료 처리 (프론트엔드에서 결제 완료 후 호출)
     */
    @Operation(summary = "결제 완료 처리", description = "프론트엔드에서 결제 완료 후 결과를 전달받아 처리합니다.")
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> completePayment(
            @Valid @RequestBody PaymentCompleteRequestDto request,
            @AuthenticationPrincipal String userId) {
        
        log.info("결제 완료 처리 - 사용자: {}, 결제ID: {}", userId, request.getPaymentId());
        
        // PortOne API로 결제 검증 후 DB 저장
        PaymentResponseDto response = paymentService.verifyAndSavePayment(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 빌링키 등록 완료 처리 (프론트엔드에서 빌링키 발급 후 호출)
     */
    @Operation(summary = "빌링키 등록", description = "프론트엔드에서 빌링키 발급 완료 후 결과를 저장합니다.")
    @PostMapping("/billing-key/register")
    public ResponseEntity<ApiResponse<BillingKeyResponseDto>> registerBillingKey(
            @Valid @RequestBody BillingKeyRegisterRequestDto request,
            @AuthenticationPrincipal String userId) {
        
        log.info("빌링키 등록 - 사용자: {}, 빌링키: {}", userId, request.getBillingKey());
        
        // 빌링키 정보 DB 저장
        BillingKeyResponseDto response = paymentService.registerBillingKey(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 정기결제 실행 (서버에서 스케줄러로 실행)
     * 관리자 전용 API - 실제로는 스케줄러에서 호출
     */
    @Operation(summary = "정기결제 실행", description = "등록된 빌링키로 정기결제를 실행합니다. (관리자/스케줄러 전용)")
    @PostMapping("/subscription/execute")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> executeSubscriptionPayment(
            @Valid @RequestBody SubscriptionPaymentRequestDto request) {
        
        log.info("정기결제 실행 - 가족ID: {}, 금액: {}", 
                request.getFamilyId(), request.getAmount());
        
        // 서버에서 PortOne API 호출하여 정기결제 실행
        PaymentResponseDto response = paymentService.executeSubscriptionPayment(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 결제 취소
     */
    @Operation(summary = "결제 취소", description = "승인된 결제를 취소합니다.")
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> cancelPayment(
            @PathVariable String paymentId,
            @AuthenticationPrincipal String userId) {
        
        log.info("결제 취소 요청 - 사용자: {}, 결제ID: {}", userId, paymentId);
        
        PaymentResponseDto response = paymentService.cancelPayment(paymentId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 결제 상세 조회
     */
    @Operation(summary = "결제 상세 조회", description = "결제 상세 정보를 조회합니다.")
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(
            @PathVariable String paymentId,
            @AuthenticationPrincipal String userId) {
        
        log.info("결제 상세 조회 - 사용자: {}, 결제ID: {}", userId, paymentId);
        
        PaymentResponseDto response = paymentService.getPayment(paymentId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자 결제 내역 조회
     */
    @Operation(summary = "결제 내역 조회", description = "사용자의 결제 내역을 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getPaymentHistory(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("결제 내역 조회 - 사용자: {}, 페이지: {}, 크기: {}", userId, page, size);
        
        List<PaymentResponseDto> response = paymentService.getPaymentHistory(userId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 빌링키 목록 조회
     */
    @Operation(summary = "빌링키 목록 조회", description = "사용자의 등록된 빌링키 목록을 조회합니다.")
    @GetMapping("/billing-keys")
    public ResponseEntity<ApiResponse<List<BillingKeyResponseDto>>> getBillingKeys(
            @AuthenticationPrincipal String userId) {
        
        log.info("빌링키 목록 조회 - 사용자: {}", userId);
        
        List<BillingKeyResponseDto> response = paymentService.getBillingKeys(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 빌링키 삭제
     */
    @Operation(summary = "빌링키 삭제", description = "등록된 빌링키를 삭제합니다.")
    @DeleteMapping("/billing-keys/{billingKeyId}")
    public ResponseEntity<ApiResponse<Void>> deleteBillingKey(
            @PathVariable String billingKeyId,
            @AuthenticationPrincipal String userId) {
        
        log.info("빌링키 삭제 요청 - 사용자: {}, 빌링키ID: {}", userId, billingKeyId);
        
        paymentService.deleteBillingKey(billingKeyId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 포트원 웹훅 처리
     */
    @Operation(summary = "결제 웹훅", description = "포트원으로부터 결제 상태 변경 알림을 처리합니다.")
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Void>> handleWebhook(
            @RequestBody String webhookData,
            @RequestHeader("X-Portone-Signature") String signature) {
        
        log.info("포트원 웹훅 수신 - 서명: {}", signature);
        
        paymentService.handleWebhook(webhookData, signature);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}