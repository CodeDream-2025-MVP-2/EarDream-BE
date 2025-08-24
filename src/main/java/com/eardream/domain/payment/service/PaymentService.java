package com.eardream.domain.payment.service;

import com.eardream.domain.payment.dto.*;
import com.eardream.domain.payment.entity.BillingKey;
import com.eardream.domain.payment.entity.Payment;
import com.eardream.domain.payment.mapper.PaymentMapper;
import com.eardream.global.exception.BusinessException;
import com.eardream.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 결제 서비스
 * 포트원 V2 SDK와 연동되는 결제 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PortOneService portOneService;

    @Value("${portone.store-id}")
    private String storeId;

    /**
     * 결제 완료 검증 및 저장
     * 프론트엔드에서 결제 완료 후 호출
     */
    public PaymentResponseDto verifyAndSavePayment(PaymentCompleteRequestDto request, String userId) {
        try {
            // 1. PortOne API로 결제 검증
            PaymentVerificationResult verification = portOneService.verifyPayment(
                    request.getPaymentId(), request.getImpUid());
            
            if (!verification.isValid()) {
                throw new BusinessException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
            }

            // 2. 결제 정보 DB 저장
            Payment payment = Payment.builder()
                    .paymentId(request.getPaymentId())
                    .orderId(request.getMerchantUid())
                    .userId(userId)
                    .familyId(request.getFamilyId())
                    .amount(verification.getAmount())
                    .currency("KRW")
                    .paymentMethod(verification.getPayMethod())
                    .status(Payment.PaymentStatus.APPROVED)
                    .type(Payment.PaymentType.BILLING_KEY)
                    .portoneTransactionId(request.getImpUid())
                    .buyerName(verification.getBuyerName())
                    .buyerEmail(verification.getBuyerEmail())
                    .buyerPhone(verification.getBuyerTel())
                    .productName(verification.getName())
                    .requestedAt(LocalDateTime.now())
                    .approvedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            paymentMapper.insertPayment(payment);

            log.info("결제 검증 및 저장 완료 - 결제ID: {}, 사용자: {}", payment.getPaymentId(), userId);

            return PaymentResponseDto.fromEntity(payment);

        } catch (Exception e) {
            log.error("결제 검증 실패 - 결제ID: {}, 사용자: {}", request.getPaymentId(), userId, e);
            throw new BusinessException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    /**
     * 빌링키 등록
     * 프론트엔드에서 빌링키 발급 완료 후 호출
     */
    public BillingKeyResponseDto registerBillingKey(BillingKeyRegisterRequestDto request, String userId) {
        try {
            // 1. 기존 빌링키 확인 (중복 방지)
            if (paymentMapper.existsBillingKeyByCustomerUid(request.getCustomerUid())) {
                throw new BusinessException(ErrorCode.BILLING_KEY_ALREADY_EXISTS);
            }

            // 2. 빌링키 정보 저장
            BillingKey billingKey = BillingKey.builder()
                    .billingKeyId(UUID.randomUUID().toString())
                    .userId(userId)
                    .familyId(request.getFamilyId())
                    .customerUid(request.getCustomerUid())
                    .portoneKey(request.getBillingKey())
                    .cardName(request.getCardName())
                    .cardNumber(request.getCardNumber())
                    .status(BillingKey.BillingKeyStatus.ACTIVE)
                    .issuedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            paymentMapper.insertBillingKey(billingKey);

            log.info("빌링키 등록 완료 - 빌링키ID: {}, 사용자: {}", 
                    billingKey.getBillingKeyId(), userId);

            return BillingKeyResponseDto.fromEntity(billingKey);

        } catch (Exception e) {
            log.error("빌링키 등록 실패 - 사용자: {}, 고객UID: {}", userId, request.getCustomerUid(), e);
            throw new BusinessException(ErrorCode.BILLING_KEY_REGISTER_FAILED);
        }
    }

    /**
     * 정기결제 실행 (서버에서 스케줄러로 실행)
     * 관리자나 스케줄러가 호출
     */
    public PaymentResponseDto executeSubscriptionPayment(SubscriptionPaymentRequestDto request) {
        try {
            // 1. 가족 그룹의 활성 빌링키 조회
            BillingKey billingKey = paymentMapper.findActiveBillingKeyByFamilyId(request.getFamilyId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_KEY_NOT_FOUND));

            if (!BillingKey.BillingKeyStatus.ACTIVE.equals(billingKey.getStatus())) {
                throw new BusinessException(ErrorCode.BILLING_KEY_INACTIVE);
            }

            // 2. PortOne API로 정기결제 실행
            String merchantUid = "SUB_" + request.getFamilyId() + "_" + System.currentTimeMillis();
            
            PaymentResult result = portOneService.payWithBillingKey(
                    billingKey.getCustomerUid(),
                    merchantUid,
                    request.getAmount(),
                    request.getOrderName());

            // 3. 결제 정보 DB 저장
            Payment payment = Payment.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .orderId(merchantUid)
                    .userId(billingKey.getUserId())
                    .familyId(request.getFamilyId())
                    .amount(request.getAmount())
                    .currency("KRW")
                    .paymentMethod("CARD")
                    .status(result.isSuccess() ? Payment.PaymentStatus.APPROVED : Payment.PaymentStatus.FAILED)
                    .type(Payment.PaymentType.SUBSCRIPTION)
                    .billingKeyId(billingKey.getBillingKeyId())
                    .portoneTransactionId(result.getImpUid())
                    .productName(request.getOrderName())
                    .requestedAt(LocalDateTime.now())
                    .approvedAt(result.isSuccess() ? LocalDateTime.now() : null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            paymentMapper.insertPayment(payment);

            // 4. 빌링키 마지막 사용 시간 업데이트
            billingKey.setLastUsedAt(LocalDateTime.now());
            paymentMapper.updateBillingKey(billingKey);

            log.info("정기결제 실행 완료 - 결제ID: {}, 가족ID: {}, 상태: {}", 
                    payment.getPaymentId(), request.getFamilyId(), payment.getStatus());

            return PaymentResponseDto.fromEntity(payment);

        } catch (Exception e) {
            log.error("정기결제 실행 실패 - 가족ID: {}", request.getFamilyId(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_PAYMENT_FAILED);
        }
    }

    /**
     * 결제 취소
     */
    public PaymentResponseDto cancelPayment(String paymentId, String userId) {
        try {
            // 1. 결제 정보 조회 및 권한 확인
            Payment payment = paymentMapper.findPaymentById(paymentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

            if (!payment.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
            }

            if (!Payment.PaymentStatus.APPROVED.equals(payment.getStatus())) {
                throw new BusinessException(ErrorCode.PAYMENT_CANCEL_NOT_ALLOWED);
            }

            // 2. 포트원 결제 취소 요청
            portOneService.cancelPayment(payment.getPortoneTransactionId(), payment.getAmount());

            // 3. 결제 상태 업데이트
            payment.setStatus(Payment.PaymentStatus.CANCELLED);
            payment.setCancelledAt(LocalDateTime.now());
            paymentMapper.updatePayment(payment);

            log.info("결제 취소 완료 - 결제ID: {}, 사용자: {}", paymentId, userId);

            return PaymentResponseDto.fromEntity(payment);

        } catch (Exception e) {
            log.error("결제 취소 실패 - 결제ID: {}, 사용자: {}", paymentId, userId, e);
            throw new BusinessException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }
    }

    /**
     * 결제 상세 조회
     */
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(String paymentId, String userId) {
        Payment payment = paymentMapper.findPaymentById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
        }

        return PaymentResponseDto.fromEntity(payment);
    }

    /**
     * 결제 내역 조회
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentHistory(String userId, int page, int size) {
        int offset = page * size;
        List<Payment> payments = paymentMapper.findPaymentsByUserId(userId, offset, size);
        
        return payments.stream()
                .map(PaymentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 빌링키 목록 조회
     */
    @Transactional(readOnly = true)
    public List<BillingKeyResponseDto> getBillingKeys(String userId) {
        List<BillingKey> billingKeys = paymentMapper.findBillingKeysByUserId(userId);
        
        return billingKeys.stream()
                .map(BillingKeyResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 빌링키 삭제
     */
    public void deleteBillingKey(String billingKeyId, String userId) {
        BillingKey billingKey = paymentMapper.findBillingKeyById(billingKeyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_KEY_NOT_FOUND));

        if (!billingKey.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BILLING_KEY_ACCESS_DENIED);
        }

        // 포트원 빌링키 삭제
        portOneService.deleteBillingKey(billingKey.getPortoneKey());

        // DB에서 빌링키 상태 변경
        billingKey.setStatus(BillingKey.BillingKeyStatus.DELETED);
        paymentMapper.updateBillingKey(billingKey);

        log.info("빌링키 삭제 완료 - 빌링키ID: {}, 사용자: {}", billingKeyId, userId);
    }

    /**
     * 웹훅 처리
     */
    public void handleWebhook(String webhookData, String signature) {
        try {
            // 1. 서명 검증
            if (!portOneService.verifyWebhookSignature(webhookData, signature)) {
                log.warn("웹훅 서명 검증 실패");
                return;
            }

            // 2. 웹훅 데이터 파싱 및 처리
            portOneService.processWebhookData(webhookData);

            log.info("웹훅 처리 완료");

        } catch (Exception e) {
            log.error("웹훅 처리 실패", e);
            throw new BusinessException(ErrorCode.WEBHOOK_PROCESS_FAILED);
        }
    }

}