package com.eardream.domain.payment.service;

import com.eardream.domain.payment.dto.*;
import com.eardream.global.exception.BusinessException;
import com.eardream.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 포트원 V2 SDK 연동 서비스
 * 실제 포트원 API 호출 및 응답 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortOneService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${portone.api-url}")
    private String apiUrl;

    @Value("${portone.api-key}")
    private String apiKey;

    @Value("${portone.api-secret}")
    private String apiSecret;

    @Value("${portone.store-id}")
    private String storeId;

    @Value("${portone.webhook-secret}")
    private String webhookSecret;

    /**
     * 포트원 액세스 토큰 발급
     */
    private String getAccessToken() {
        try {
            String url = apiUrl + "/login/api-secret";
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("apiSecret", apiSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("accessToken").asText();

        } catch (Exception e) {
            log.error("포트원 액세스 토큰 발급 실패", e);
            throw new BusinessException(ErrorCode.PORTONE_TOKEN_FAILED);
        }
    }

    /**
     * 결제 검증 (PortOne API 호출)
     * 프론트에서 결제 완료 후 백엔드에서 검증
     */
    public PaymentVerificationResult verifyPayment(String paymentId, String impUid) {
        try {
            String accessToken = getAccessToken();
            String url = apiUrl + "/payments/" + impUid;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<?> httpRequest = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, httpRequest, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            // 결제 검증 결과 매핑
            return PaymentVerificationResult.builder()
                    .valid("paid".equals(jsonNode.get("status").asText()))
                    .impUid(impUid)
                    .merchantUid(jsonNode.get("merchantUid").asText())
                    .amount(new BigDecimal(jsonNode.get("amount").asText()))
                    .payMethod(jsonNode.get("payMethod").asText())
                    .buyerName(jsonNode.get("buyerName").asText())
                    .buyerEmail(jsonNode.get("buyerEmail").asText())
                    .buyerTel(jsonNode.get("buyerTel").asText())
                    .name(jsonNode.get("name").asText())
                    .status(jsonNode.get("status").asText())
                    .failReason(jsonNode.has("failReason") ? jsonNode.get("failReason").asText() : null)
                    .build();

        } catch (Exception e) {
            log.error("포트원 결제 검증 실패 - 결제ID: {}, impUid: {}", paymentId, impUid, e);
            throw new BusinessException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    /**
     * 빌링키를 통한 결제 (서버에서 실행)
     * 스케줄러나 관리자가 호출
     */
    public PaymentResult payWithBillingKey(String customerUid, String merchantUid, BigDecimal amount, String orderName) {
        try {
            String accessToken = getAccessToken();
            String url = apiUrl + "/payments/again";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("storeId", storeId);
            requestBody.put("customerUid", customerUid);
            requestBody.put("merchantUid", merchantUid);
            requestBody.put("amount", amount);
            requestBody.put("orderName", orderName);
            requestBody.put("currency", "KRW");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, httpRequest, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            // 결제 결과 매핑
            return PaymentResult.builder()
                    .success("paid".equals(jsonNode.get("status").asText()))
                    .impUid(jsonNode.get("impUid").asText())
                    .merchantUid(merchantUid)
                    .amount(amount)
                    .payMethod("card")
                    .cardName(jsonNode.has("cardName") ? jsonNode.get("cardName").asText() : null)
                    .cardNumber(jsonNode.has("cardNumber") ? jsonNode.get("cardNumber").asText() : null)
                    .status(jsonNode.get("status").asText())
                    .failReason(jsonNode.has("failReason") ? jsonNode.get("failReason").asText() : null)
                    .receiptUrl(jsonNode.has("receiptUrl") ? jsonNode.get("receiptUrl").asText() : null)
                    .build();

        } catch (Exception e) {
            log.error("포트원 빌링키 결제 실패 - customerUid: {}, merchantUid: {}", customerUid, merchantUid, e);
            throw new BusinessException(ErrorCode.PORTONE_BILLING_PAYMENT_FAILED);
        }
    }


    /**
     * 결제 취소
     */
    public void cancelPayment(String transactionId, BigDecimal amount) {
        try {
            String accessToken = getAccessToken();
            String url = apiUrl + "/payments/" + transactionId + "/cancel";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", amount);
            requestBody.put("reason", "고객 요청에 의한 취소");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);
            
            restTemplate.exchange(url, HttpMethod.POST, httpRequest, String.class);

        } catch (Exception e) {
            log.error("포트원 결제 취소 실패 - 거래ID: {}", transactionId, e);
            throw new BusinessException(ErrorCode.PORTONE_CANCEL_FAILED);
        }
    }

    /**
     * 빌링키 삭제
     */
    public void deleteBillingKey(String billingKey) {
        try {
            String accessToken = getAccessToken();
            String url = apiUrl + "/billing-keys/" + billingKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<?> httpRequest = new HttpEntity<>(headers);
            
            restTemplate.exchange(url, HttpMethod.DELETE, httpRequest, String.class);

        } catch (Exception e) {
            log.error("포트원 빌링키 삭제 실패 - 빌링키: {}", billingKey, e);
            throw new BusinessException(ErrorCode.PORTONE_BILLING_KEY_DELETE_FAILED);
        }
    }

    /**
     * 웹훅 서명 검증
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(hash);

            return computedSignature.equals(signature);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("웹훅 서명 검증 실패", e);
            return false;
        }
    }

    /**
     * 웹훅 데이터 처리
     */
    public void processWebhookData(String webhookData) {
        try {
            JsonNode jsonNode = objectMapper.readTree(webhookData);
            String type = jsonNode.get("type").asText();
            
            switch (type) {
                case "Transaction.Paid":
                    handlePaymentCompleted(jsonNode);
                    break;
                case "Transaction.Failed":
                    handlePaymentFailed(jsonNode);
                    break;
                case "Transaction.Cancelled":
                    handlePaymentCancelled(jsonNode);
                    break;
                default:
                    log.info("처리되지 않은 웹훅 타입: {}", type);
            }

        } catch (Exception e) {
            log.error("웹훅 데이터 처리 실패", e);
            throw new BusinessException(ErrorCode.WEBHOOK_PROCESS_FAILED);
        }
    }

    /**
     * 결제 완료 웹훅 처리
     */
    private void handlePaymentCompleted(JsonNode data) {
        // TODO: 결제 완료 처리 로직 구현
        log.info("결제 완료 웹훅 처리: {}", data.get("paymentId").asText());
    }

    /**
     * 결제 실패 웹훅 처리
     */
    private void handlePaymentFailed(JsonNode data) {
        // TODO: 결제 실패 처리 로직 구현
        log.info("결제 실패 웹훅 처리: {}", data.get("paymentId").asText());
    }

    /**
     * 결제 취소 웹훅 처리
     */
    private void handlePaymentCancelled(JsonNode data) {
        // TODO: 결제 취소 처리 로직 구현
        log.info("결제 취소 웹훅 처리: {}", data.get("paymentId").asText());
    }

    /**
     * 결제 수단별 채널키 반환
     */
    private String getChannelKey(String paymentMethod) {
        // TODO: 실제 포트원 설정에 맞는 채널키 반환
        return switch (paymentMethod) {
            case "CARD" -> "channel-key-card";
            case "KAKAOPAY" -> "channel-key-kakaopay";
            case "BANK" -> "channel-key-bank";
            default -> "channel-key-default";
        };
    }
}